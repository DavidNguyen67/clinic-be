# MedCare — Backend Roadmap (Java Spring Boot)

> Roadmap triển khai backend cho hệ thống đặt lịch khám bệnh **MedCare**  
> Database: PostgreSQL (schema `medcare_v1_init.sql` đã có)  
> Frontend: React + TypeScript, 33 routes, 5 roles, SWR global state  

---

## Mục lục

1. [Tech Stack](#1-tech-stack)
2. [Kiến trúc dự án](#2-kiến-trúc-dự-án)
3. [Phase 1 — Foundation (Tuần 1–2)](#3-phase-1--foundation-tuần-12)
4. [Phase 2 — Authentication & Authorization (Tuần 3–4)](#4-phase-2--authentication--authorization-tuần-34)
5. [Phase 3 — Core Booking Flow (Tuần 5–7)](#5-phase-3--core-booking-flow-tuần-57)
6. [Phase 4 — Medical Records & Prescriptions (Tuần 8–9)](#6-phase-4--medical-records--prescriptions-tuần-89)
7. [Phase 5 — Payment Integration (Tuần 10–11)](#7-phase-5--payment-integration-tuần-1011)
8. [Phase 6 — Realtime (WebSocket + Notifications) (Tuần 12–13)](#8-phase-6--realtime-websocket--notifications-tuần-1213)
9. [Phase 7 — Admin & Reports (Tuần 14–15)](#9-phase-7--admin--reports-tuần-1415)
10. [Phase 8 — Security, Testing & Deploy (Tuần 16–17)](#10-phase-8--security-testing--deploy-tuần-1617)
11. [API Endpoint Map — Toàn bộ](#11-api-endpoint-map--toàn-bộ)
12. [Entity Map — 24 Tables → JPA](#12-entity-map--24-tables--jpa)
13. [Kết nối Frontend (SWR → API)](#13-kết-nối-frontend-swr--api)
14. [Timeline Tổng](#14-timeline-tổng)

---

## 1. Tech Stack

| Lớp | Công nghệ | Ghi chú |
|---|---|---|
| **Framework** | Spring Boot 3.3.x | Java 21 (LTS) |
| **Security** | Spring Security 6 + JWT | Access token 15p, Refresh token 30 ngày |
| **Database** | PostgreSQL 15+ | Schema đã có sẵn `medcare_v1_init.sql` |
| **ORM** | Spring Data JPA + Hibernate | Enum mapping khớp 13 enums SQL |
| **Migration** | Flyway | Quản lý version DB, import SQL có sẵn |
| **Cache** | Redis (Spring Data Redis) | OTP, slot locking, session blacklist |
| **Realtime** | Spring WebSocket (STOMP) | Chat bác sĩ-bệnh nhân, notifications |
| **Storage** | MinIO / AWS S3 (Spring Cloud AWS) | Avatar, PDF bệnh án, kết quả xét nghiệm |
| **Email** | Spring Mail + Thymeleaf | Template email xác nhận lịch hẹn |
| **SMS** | Twilio SDK | OTP, nhắc lịch |
| **Payment** | VNPay SDK + MoMo API | Tích hợp cổng thanh toán |
| **Docs** | Springdoc OpenAPI (Swagger UI) | `/api/docs` |
| **Build** | Maven / Gradle | |
| **Container** | Docker + Docker Compose | Local dev |
| **Deploy** | Railway / Render + Supabase | Hoặc VPS + Nginx |
| **Monitoring** | Spring Actuator + Micrometer | Health check, metrics |

### Dependencies chính (pom.xml)

```xml
<!-- Core -->
<dependency>spring-boot-starter-web</dependency>
<dependency>spring-boot-starter-data-jpa</dependency>
<dependency>spring-boot-starter-security</dependency>
<dependency>spring-boot-starter-validation</dependency>
<dependency>spring-boot-starter-mail</dependency>
<dependency>spring-boot-starter-websocket</dependency>
<dependency>spring-boot-starter-data-redis</dependency>
<dependency>spring-boot-starter-actuator</dependency>

<!-- JWT -->
<dependency>io.jsonwebtoken:jjwt-api:0.12.x</dependency>
<dependency>io.jsonwebtoken:jjwt-impl:0.12.x</dependency>
<dependency>io.jsonwebtoken:jjwt-jackson:0.12.x</dependency>

<!-- DB & Migration -->
<dependency>org.postgresql:postgresql</dependency>
<dependency>org.flywaydb:flyway-core</dependency>

<!-- Storage -->
<dependency>io.minio:minio:8.5.x</dependency>

<!-- Docs -->
<dependency>org.springdoc:springdoc-openapi-starter-webmvc-ui:2.x</dependency>

<!-- Utilities -->
<dependency>org.mapstruct:mapstruct:1.5.x</dependency>
<dependency>org.projectlombok:lombok</dependency>
```

---

## 2. Kiến trúc dự án

### Cấu trúc thư mục

```
medcare-backend/
├── src/main/java/vn/medcare/
│   │
│   ├── config/                         # Cấu hình toàn cục
│   │   ├── SecurityConfig.java         # Spring Security, CORS, filter chain
│   │   ├── JwtConfig.java              # JWT properties
│   │   ├── RedisConfig.java            # Redis template, serializer
│   │   ├── WebSocketConfig.java        # STOMP endpoint, message broker
│   │   ├── SwaggerConfig.java          # OpenAPI, Bearer auth scheme
│   │   ├── StorageConfig.java          # MinIO / S3 client
│   │   └── MailConfig.java             # JavaMailSender
│   │
│   ├── common/                         # Shared utilities
│   │   ├── dto/
│   │   │   ├── ApiResponse.java        # { success, message, data, errors }
│   │   │   ├── PageResponse.java       # { items, total, page, size }
│   │   │   └── ErrorResponse.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── AppException.java       # Runtime exception base
│   │   │   ├── NotFoundException.java
│   │   │   ├── ForbiddenException.java
│   │   │   └── BusinessException.java  # Logic errors (slot đã đặt, v.v.)
│   │   ├── enums/                      # Map 13 enums từ SQL
│   │   │   ├── UserRole.java
│   │   │   ├── UserStatus.java
│   │   │   ├── AppointmentStatus.java
│   │   │   ├── InvoiceStatus.java
│   │   │   ├── PaymentMethod.java
│   │   │   ├── ReviewStatus.java
│   │   │   ├── LabTestStatus.java
│   │   │   ├── MedicineStatus.java
│   │   │   ├── NotificationType.java
│   │   │   ├── Gender.java
│   │   │   ├── DayOfWeek.java
│   │   │   ├── ServiceCategory.java
│   │   │   └── MessageSenderRole.java
│   │   ├── validator/                  # Custom annotations
│   │   │   ├── PhoneValidator.java
│   │   │   └── PasswordStrengthValidator.java
│   │   └── util/
│   │       ├── DateTimeUtils.java
│   │       ├── SlotGenerator.java      # Tạo time slots từ schedule
│   │       └── PdfGenerator.java       # Export bệnh án PDF
│   │
│   ├── security/
│   │   ├── JwtTokenProvider.java       # Tạo / verify JWT
│   │   ├── JwtAuthFilter.java          # OncePerRequestFilter
│   │   ├── UserDetailsServiceImpl.java # Load user từ DB
│   │   ├── CustomUserDetails.java      # Principal wrapper
│   │   └── TokenBlacklist.java         # Redis-based logout blacklist
│   │
│   ├── module/                         # Feature modules (1 folder = 1 domain)
│   │   ├── auth/
│   │   ├── user/
│   │   ├── patient/
│   │   ├── doctor/
│   │   ├── appointment/
│   │   ├── medicalrecord/
│   │   ├── prescription/
│   │   ├── labtest/
│   │   ├── invoice/
│   │   ├── payment/
│   │   ├── notification/
│   │   ├── message/
│   │   ├── review/
│   │   ├── service/
│   │   ├── medicine/
│   │   ├── specialty/
│   │   └── report/
│   │
│   └── MedcareApplication.java
│
├── src/main/resources/
│   ├── application.yml                 # Main config
│   ├── application-dev.yml             # Dev overrides
│   ├── application-prod.yml            # Prod overrides
│   ├── db/migration/                   # Flyway
│   │   └── V1__init_schema.sql         # Copy từ medcare_v1_init.sql
│   └── templates/email/                # Thymeleaf email templates
│       ├── confirm-appointment.html
│       ├── reminder.html
│       ├── otp.html
│       └── invoice.html
│
├── docker-compose.yml                  # postgres + redis + minio
└── Dockerfile
```

### Cấu trúc mỗi module

```
module/appointment/
├── entity/
│   └── Appointment.java          # @Entity map table appointments
├── repository/
│   └── AppointmentRepository.java # JpaRepository + custom queries
├── dto/
│   ├── request/
│   │   ├── CreateAppointmentRequest.java
│   │   └── UpdateAppointmentRequest.java
│   └── response/
│       ├── AppointmentResponse.java
│       └── AppointmentDetailResponse.java
├── mapper/
│   └── AppointmentMapper.java    # MapStruct: Entity ↔ DTO
├── service/
│   ├── AppointmentService.java   # Interface
│   └── AppointmentServiceImpl.java
└── controller/
    └── AppointmentController.java # @RestController
```

---

## 3. Phase 1 — Foundation (Tuần 1–2)

> **Mục tiêu:** Project chạy được, kết nối DB, Flyway migration, Docker, health check

### 3.1 Khởi tạo project

```bash
# Spring Initializr
curl https://start.spring.io/starter.zip \
  -d dependencies=web,data-jpa,security,validation,actuator,postgresql,flyway,lombok,data-redis \
  -d groupId=vn.medcare \
  -d artifactId=medcare-backend \
  -d javaVersion=21 \
  -o medcare-backend.zip
```

### 3.2 application.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/medcare
    username: ${DB_USER:medcare}
    password: ${DB_PASS:secret}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  jpa:
    hibernate:
      ddl-auto: validate          # Flyway quản lý schema, JPA chỉ validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: 6379
      password: ${REDIS_PASS:}

app:
  jwt:
    access-secret: ${JWT_ACCESS_SECRET}
    refresh-secret: ${JWT_REFRESH_SECRET}
    access-expiry-ms: 900000        # 15 phút
    refresh-expiry-ms: 2592000000   # 30 ngày
  cors:
    allowed-origins: ${FRONTEND_URL:http://localhost:5173}
  upload:
    max-file-size: 10MB
```

### 3.3 Docker Compose (local dev)

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: medcare
      POSTGRES_USER: medcare
      POSTGRES_PASSWORD: secret
    ports: ["5432:5432"]
    volumes:
      - pgdata:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports: ["6379:6379"]

  minio:
    image: minio/minio
    command: server /data --console-address ":9001"
    ports: ["9000:9000", "9001:9001"]
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin123

volumes:
  pgdata:
```

### 3.4 Flyway Migration

- Copy `medcare_v1_init.sql` → `src/main/resources/db/migration/V1__init_schema.sql`
- Flyway tự chạy khi app start, tạo đủ 24 tables + enums + seed data

### 3.5 Enum mapping với PostgreSQL

```java
// Mỗi enum trong SQL cần @Converter riêng
@Converter(autoApply = true)
public class AppointmentStatusConverter
    implements AttributeConverter<AppointmentStatus, String> {
  @Override
  public String convertToDatabaseColumn(AppointmentStatus status) {
    return status.getValue();          // "pending", "confirmed", ...
  }
  @Override
  public AppointmentStatus convertToEntityAttribute(String value) {
    return AppointmentStatus.fromValue(value);
  }
}
```

### 3.6 Global Response Format

Tất cả API trả về chuẩn này — Frontend SWR sẽ expect format nhất quán:

```java
// Success
{
  "success": true,
  "data": { ... },
  "message": "OK"
}

// Error
{
  "success": false,
  "error": {
    "code": "APPOINTMENT_SLOT_TAKEN",
    "message": "Slot này đã có người đặt",
    "field": "slotId"   // null nếu không phải validation error
  }
}

// Paginated list
{
  "success": true,
  "data": {
    "items": [...],
    "total": 120,
    "page": 1,
    "size": 20,
    "totalPages": 6
  }
}
```

### 3.7 Deliverables Phase 1

- [ ] Project khởi tạo, compile không lỗi
- [ ] Docker Compose chạy được Postgres + Redis + MinIO
- [ ] Flyway migration thành công — 24 tables tồn tại
- [ ] Health check `GET /actuator/health` → `{ status: "UP" }`
- [ ] Swagger UI truy cập được tại `/api/docs`
- [ ] GlobalExceptionHandler xử lý 404, 400, 500

---

## 4. Phase 2 — Authentication & Authorization (Tuần 3–4)

> **Mục tiêu:** Đăng ký/đăng nhập/đăng xuất, JWT, phân quyền 5 roles, OTP email

### 4.1 Entity: User

```java
@Entity @Table(name = "users")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(columnDefinition = "user_role")
    @Convert(converter = UserRoleConverter.class)
    private UserRole role;

    @Column(columnDefinition = "user_status")
    @Convert(converter = UserStatusConverter.class)
    private UserStatus status;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    private Instant createdAt;
    private Instant updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Patient patient;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Doctor doctor;
}
```

### 4.2 Auth Endpoints

| Method | Endpoint | Body | Response | Mô tả |
|---|---|---|---|---|
| POST | `/api/auth/register` | `{ name, email, phone, password }` | `{ user, accessToken, refreshToken }` | Đăng ký bệnh nhân |
| POST | `/api/auth/login` | `{ email, password }` | `{ user, accessToken, refreshToken }` | Đăng nhập |
| POST | `/api/auth/refresh` | `{ refreshToken }` | `{ accessToken }` | Làm mới access token |
| POST | `/api/auth/logout` | `{ refreshToken }` | `204` | Blacklist refresh token |
| POST | `/api/auth/forgot-password` | `{ email }` | `{ message }` | Gửi OTP 6 số qua email |
| POST | `/api/auth/verify-otp` | `{ email, otp }` | `{ resetToken }` | Xác minh OTP |
| POST | `/api/auth/reset-password` | `{ resetToken, newPassword }` | `204` | Đặt lại mật khẩu |
| GET | `/api/auth/me` | — | `{ user, profile }` | Thông tin user hiện tại |
| POST | `/api/auth/change-password` | `{ currentPassword, newPassword }` | `204` | Đổi mật khẩu |

### 4.3 JWT Flow

```
Login Request
  → Validate email/password
  → BCrypt compare password_hash
  → Tạo Access Token (15p, payload: { userId, role, name })
  → Tạo Refresh Token (30 ngày, lưu hash vào Redis: key="RT:{userId}")
  → Trả về cả 2 token

Request có Access Token
  → JwtAuthFilter intercept
  → Verify chữ ký + expiry
  → Check token không bị blacklist (Redis: "BL:{jti}")
  → Set SecurityContext

Refresh Token
  → Verify refresh token
  → Kiểm tra hash trong Redis khớp
  → Tạo Access Token mới (không tạo Refresh Token mới)

Logout
  → Thêm jti của Access Token vào Redis BL:{jti} (TTL = thời gian còn lại)
  → Xóa Refresh Token khỏi Redis
```

### 4.4 OTP Flow (Email)

```
POST /forgot-password
  → Validate email tồn tại
  → Tạo OTP 6 số ngẫu nhiên
  → Lưu Redis: "OTP:{email}" = otpHash (TTL 10 phút, max 3 lần gửi/giờ)
  → Gửi email template otp.html qua Spring Mail
  → Response: { message: "OTP đã được gửi" }

POST /verify-otp
  → Lấy OTP từ Redis, BCrypt compare
  → Nếu đúng: tạo resetToken (UUID), lưu Redis "RST:{resetToken}" = userId (TTL 15p)
  → Response: { resetToken }

POST /reset-password
  → Lấy userId từ Redis "RST:{resetToken}"
  → BCrypt hash new password, update DB
  → Xóa resetToken khỏi Redis
```

### 4.5 Role Guards

```java
// Annotation-based
@PreAuthorize("hasRole('DOCTOR')")
@GetMapping("/doctor/appointments")
public ResponseEntity<?> getMyAppointments() { ... }

// Method-level multi-role
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
@PatchMapping("/{id}/confirm")
public ResponseEntity<?> confirmAppointment(...) { ... }

// Ownership check (bệnh nhân chỉ xem của mình)
@PreAuthorize("hasRole('PATIENT') and @appointmentService.isOwner(#id, principal.userId)")
@GetMapping("/{id}")
public ResponseEntity<?> getAppointment(@PathVariable UUID id) { ... }
```

### 4.6 Deliverables Phase 2

- [ ] `POST /api/auth/register` và `/login` trả JWT hợp lệ
- [ ] JwtAuthFilter hoạt động, `GET /api/auth/me` trả user info
- [ ] `@PreAuthorize` block đúng role, trả 403 khi không đủ quyền
- [ ] OTP gửi email thật (test với Mailtrap hoặc Gmail SMTP)
- [ ] Refresh token và logout hoạt động (blacklist Redis)
- [ ] Rate limiting login: max 5 lần/phút/IP

---

## 5. Phase 3 — Core Booking Flow (Tuần 5–7)

> **Mục tiêu:** Luồng quan trọng nhất: Tìm bác sĩ → Xem lịch → Đặt lịch → Xác nhận → Check-in

### 5.1 Specialty & Doctor

```
GET  /api/specialties                   # Public: danh sách chuyên khoa
GET  /api/doctors                       # Public: danh sách bác sĩ (filter, search, paginate)
GET  /api/doctors/{id}                  # Public: chi tiết bác sĩ
GET  /api/doctors/{id}/schedule         # Public: lịch làm việc theo tuần
GET  /api/doctors/{id}/slots?date=      # Public: danh sách slot trống theo ngày
GET  /api/doctors/{id}/reviews          # Public: đánh giá
```

**Query params cho `/api/doctors`:**

```
?specialtyId=uuid
&name=keyword
&minRating=4.0
&maxFee=500000
&date=2026-04-13     # chỉ lấy bác sĩ có lịch ngày này
&page=0&size=12
&sort=rating,desc
```

### 5.2 Slot Generation

Logic tính slot trống từ `doctor_schedules` + `appointments`:

```java
@Service
public class SlotGeneratorService {

  public List<TimeSlotDto> getAvailableSlots(UUID doctorId, LocalDate date) {
    DayOfWeek dow = date.getDayOfWeek(); // Map sang enum SQL

    // 1. Lấy schedule của bác sĩ ngày đó
    List<DoctorSchedule> schedules = scheduleRepo
        .findByDoctorIdAndDayOfWeek(doctorId, dow);

    // 2. Lấy các slot đã bị đặt (status != cancelled)
    List<LocalTime> bookedTimes = appointmentRepo
        .findBookedTimesOnDate(doctorId, date);

    // 3. Lấy doctor_leaves — bác sĩ nghỉ phép hôm đó không?
    if (leaveRepo.existsLeaveOnDate(doctorId, date)) return List.of();

    // 4. Tạo slots theo slotDurationMinutes
    List<TimeSlotDto> slots = new ArrayList<>();
    for (DoctorSchedule s : schedules) {
      LocalTime cursor = s.getStartTime();
      while (cursor.plusMinutes(s.getSlotDurationMinutes())
                   .compareTo(s.getEndTime()) <= 0) {
        boolean available = !bookedTimes.contains(cursor);
        slots.add(new TimeSlotDto(cursor, available));
        cursor = cursor.plusMinutes(s.getSlotDurationMinutes());
      }
    }
    return slots;
  }
}
```

### 5.3 Appointment Booking — Luồng chi tiết

```
POST /api/appointments
Body: { doctorId, date, time, reason, serviceType }

1. Validate bệnh nhân đã login (role = PATIENT)
2. Kiểm tra doctor tồn tại và status = active
3. Kiểm tra doctor có lịch ngày đó không
4. Kiểm tra slot chưa bị đặt (DB query)
5. LOCK slot bằng Redis:
   key = "SLOT:{doctorId}:{date}:{time}"
   TTL = 5 phút (nếu không thanh toán sẽ tự release)
   SET NX (set if not exists) — chỉ 1 request thành công
6. Tạo Appointment record (status = PENDING)
7. Gửi notification cho Staff và Patient
8. Trả về appointment với instructions
```

```java
// Redis Slot Locking (Lua script — atomic)
@Service
public class SlotLockService {
  private static final String LOCK_PREFIX = "SLOT:";
  private static final long LOCK_TTL_SECONDS = 300; // 5 phút

  public boolean tryLock(UUID doctorId, LocalDate date, LocalTime time, UUID appointmentId) {
    String key = LOCK_PREFIX + doctorId + ":" + date + ":" + time;
    String value = appointmentId.toString();
    // SET key value NX EX 300
    return Boolean.TRUE.equals(
      redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(LOCK_TTL_SECONDS))
    );
  }

  public void releaseLock(UUID doctorId, LocalDate date, LocalTime time) {
    String key = LOCK_PREFIX + doctorId + ":" + date + ":" + time;
    redisTemplate.delete(key);
  }
}
```

### 5.4 Appointment Endpoints

| Method | Endpoint | Role | Mô tả |
|---|---|---|---|
| POST | `/api/appointments` | Patient | Đặt lịch mới |
| GET | `/api/appointments` | Patient/Doctor/Staff | List (filter theo role) |
| GET | `/api/appointments/{id}` | Auth | Chi tiết |
| PATCH | `/api/appointments/{id}/cancel` | Patient/Staff | Hủy lịch |
| PATCH | `/api/appointments/{id}/confirm` | Staff | Lễ tân xác nhận |
| PATCH | `/api/appointments/{id}/checkin` | Staff | Check-in bệnh nhân |
| PATCH | `/api/appointments/{id}/start` | Doctor | Bắt đầu khám |
| PATCH | `/api/appointments/{id}/complete` | Doctor | Hoàn thành khám |
| GET | `/api/appointments/today` | Doctor/Staff | Lịch hẹn hôm nay |
| GET | `/api/appointments/queue` | Staff | Hàng chờ hiện tại |

**Business Rules cần enforce:**

```
Hủy lịch (PATIENT):
  - Chỉ hủy được khi status = PENDING hoặc CONFIRMED
  - Hủy trước 2 giờ → không phạt
  - Hủy trong vòng 2 giờ → ghi chú phí hủy (tuỳ chính sách)

Hủy lịch (STAFF):
  - Có thể hủy bất kỳ lúc nào
  - Phải nhập lý do

Trạng thái chuyển đổi hợp lệ:
  PENDING → CONFIRMED (Staff)
  CONFIRMED → WAITING (khi bệnh nhân đến, Staff)
  WAITING → CHECKED_IN (Staff check-in)
  CHECKED_IN → IN_PROGRESS (Doctor bắt đầu)
  IN_PROGRESS → COMPLETED (Doctor hoàn thành)
  PENDING | CONFIRMED → CANCELLED (Patient/Staff)
  CONFIRMED → NO_SHOW (Staff — hết giờ không đến)
```

### 5.5 Doctor Schedule Management

| Method | Endpoint | Role | Mô tả |
|---|---|---|---|
| GET | `/api/doctor/schedule` | Doctor | Lịch làm việc của mình |
| POST | `/api/doctor/schedule` | Doctor | Thêm ca làm việc |
| DELETE | `/api/doctor/schedule/{id}` | Doctor | Xóa ca |
| POST | `/api/doctor/leaves` | Doctor | Đăng ký nghỉ phép |
| GET | `/api/doctor/leaves` | Doctor/Admin | Danh sách nghỉ phép |
| PATCH | `/api/doctor/leaves/{id}/approve` | Admin | Duyệt nghỉ phép |

### 5.6 Patient Profile

| Method | Endpoint | Role | Mô tả |
|---|---|---|---|
| GET | `/api/patient/profile` | Patient | Thông tin hồ sơ |
| PUT | `/api/patient/profile` | Patient | Cập nhật hồ sơ |
| GET | `/api/patient/appointments` | Patient | Lịch hẹn của tôi |
| GET | `/api/patient/history` | Patient | Lịch sử khám |

### 5.7 Search

```
GET /api/search?q=keyword&type=doctor|specialty|service
```

Tìm kiếm full-text trên: `doctors.name`, `specialties.name`, `services.name`

```sql
-- PostgreSQL full-text search
SELECT * FROM doctors d
JOIN users u ON d.user_id = u.id
WHERE to_tsvector('simple', u.name || ' ' || d.specialty_name)
      @@ plainto_tsquery('simple', :query)
ORDER BY ts_rank(...) DESC;
```

### 5.8 Deliverables Phase 3

- [ ] `GET /api/doctors` trả danh sách đúng với filter
- [ ] `GET /api/doctors/{id}/slots?date=` trả slot trống chính xác
- [ ] `POST /api/appointments` đặt lịch thành công, lock slot Redis
- [ ] Double booking bị block (2 request cùng lúc chỉ 1 thành công)
- [ ] Appointment status transitions hoạt động đúng business rules
- [ ] Gửi email xác nhận sau khi đặt lịch
- [ ] Nhắc lịch tự động trước 24h và 1h (dùng `@Scheduled`)

---

## 6. Phase 4 — Medical Records & Prescriptions (Tuần 8–9)

> **Mục tiêu:** Bác sĩ tạo bệnh án → đơn thuốc → yêu cầu xét nghiệm → bệnh nhân xem

### 6.1 Medical Record Endpoints

| Method | Endpoint | Role | Mô tả |
|---|---|---|---|
| POST | `/api/medical-records` | Doctor | Tạo bệnh án |
| GET | `/api/medical-records/{id}` | Doctor/Patient | Chi tiết |
| PUT | `/api/medical-records/{id}` | Doctor | Cập nhật (chỉ trong 24h) |
| GET | `/api/patient/medical-records` | Patient | Lịch sử bệnh án |
| GET | `/api/doctor/records` | Doctor | Bệnh án của bác sĩ |
| GET | `/api/medical-records/{id}/pdf` | Doctor/Patient | Export PDF |

### 6.2 Prescription Endpoints

| Method | Endpoint | Role | Mô tả |
|---|---|---|---|
| POST | `/api/prescriptions` | Doctor | Kê đơn (kèm medical record) |
| GET | `/api/prescriptions/{id}` | Doctor/Patient | Chi tiết đơn thuốc |
| GET | `/api/patient/prescriptions` | Patient | Tất cả đơn thuốc |
| GET | `/api/prescriptions/{id}/pdf` | Doctor/Patient | In đơn PDF |

### 6.3 Lab Test Endpoints

| Method | Endpoint | Role | Mô tả |
|---|---|---|---|
| POST | `/api/lab-tests` | Doctor | Yêu cầu xét nghiệm |
| GET | `/api/lab-tests/{id}` | Doctor/Patient/Staff | Chi tiết |
| PATCH | `/api/lab-tests/{id}/result` | Staff | Nhập kết quả + upload file |
| GET | `/api/patient/lab-results` | Patient | Kết quả xét nghiệm |

### 6.4 PDF Generation

Dùng **iText 7** hoặc **OpenPDF** để tạo PDF bệnh án/đơn thuốc:

```java
@Service
public class PdfGeneratorService {
  public byte[] generateMedicalRecordPdf(MedicalRecord record) {
    // Tạo PDF với thông tin bệnh viện, bệnh nhân, chẩn đoán, thuốc
    // Upload lên MinIO → trả về URL download
  }

  public byte[] generatePrescriptionPdf(Prescription prescription) {
    // Tạo đơn thuốc theo format chuẩn
  }
}
```

### 6.5 Deliverables Phase 4

- [ ] Doctor tạo bệnh án sau khi complete appointment
- [ ] Prescription tạo kèm theo danh sách thuốc (prescription_items)
- [ ] Lab test orders tạo được, staff upload kết quả
- [ ] Patient xem được toàn bộ lịch sử
- [ ] PDF export bệnh án và đơn thuốc

---

## 7. Phase 5 — Payment Integration (Tuần 10–11)

> **Mục tiêu:** Tích hợp VNPay + MoMo, tạo hóa đơn, xử lý callback, hoàn tiền

### 7.1 Invoice Flow

```
APPOINTMENT COMPLETED
  → Staff tạo Invoice (status = DRAFT)
  → Thêm invoice_items (phí khám, xét nghiệm, thuốc)
  → Invoice status → PENDING
  → Patient chọn phương thức thanh toán
  → Chuyển hướng sang VNPay/MoMo
  → Callback từ gateway → verify chữ ký
  → Invoice status → PAID
  → Gửi email hóa đơn điện tử
```

### 7.2 Invoice Endpoints

| Method | Endpoint | Role | Mô tả |
|---|---|---|---|
| POST | `/api/invoices` | Staff | Tạo hóa đơn |
| GET | `/api/invoices/{id}` | Staff/Patient | Chi tiết |
| GET | `/api/patient/invoices` | Patient | Lịch sử thanh toán |
| GET | `/api/staff/invoices` | Staff | Hóa đơn cần xử lý |
| GET | `/api/invoices/{id}/pdf` | Staff/Patient | Xuất hóa đơn PDF |

### 7.3 Payment Endpoints

| Method | Endpoint | Role | Mô tả |
|---|---|---|---|
| POST | `/api/payments/vnpay/create` | Patient/Staff | Tạo link thanh toán VNPay |
| GET | `/api/payments/vnpay/callback` | Public | VNPay redirect callback |
| POST | `/api/payments/vnpay/ipn` | Public | VNPay IPN webhook |
| POST | `/api/payments/momo/create` | Patient/Staff | Tạo link MoMo |
| POST | `/api/payments/momo/callback` | Public | MoMo webhook |
| POST | `/api/payments/cash` | Staff | Ghi nhận thanh toán tiền mặt |
| POST | `/api/payments/{invoiceId}/refund` | Admin | Hoàn tiền |

### 7.4 VNPay Integration

```java
@Service
public class VNPayService {

  public String createPaymentUrl(Invoice invoice, String clientIp) {
    Map<String, String> params = new TreeMap<>();
    params.put("vnp_Version", "2.1.0");
    params.put("vnp_Command", "pay");
    params.put("vnp_TmnCode", vnpayConfig.getTmnCode());
    params.put("vnp_Amount", String.valueOf(invoice.getTotal() * 100)); // VNPay * 100
    params.put("vnp_CurrCode", "VND");
    params.put("vnp_TxnRef", invoice.getId().toString());
    params.put("vnp_OrderInfo", "Thanh toan " + invoice.getId());
    params.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
    params.put("vnp_IpAddr", clientIp);
    params.put("vnp_CreateDate", LocalDateTime.now().format(dtf));

    String hashData = buildHashData(params);
    String secureHash = hmacSHA512(vnpayConfig.getHashSecret(), hashData);
    params.put("vnp_SecureHash", secureHash);

    return vnpayConfig.getPayUrl() + "?" + toQueryString(params);
  }

  public boolean verifyCallback(Map<String, String> params) {
    String receivedHash = params.remove("vnp_SecureHash");
    String computedHash = hmacSHA512(vnpayConfig.getHashSecret(), buildHashData(params));
    return MessageDigest.isEqual(receivedHash.getBytes(), computedHash.getBytes());
  }
}
```

### 7.5 Deliverables Phase 5

- [ ] Staff tạo hóa đơn với các invoice_items
- [ ] Tích hợp VNPay sandbox — thanh toán thành công
- [ ] IPN webhook cập nhật invoice status đúng
- [ ] Tích hợp MoMo sandbox
- [ ] Thanh toán tiền mặt ghi nhận được
- [ ] Email hóa đơn gửi sau khi thanh toán
- [ ] Lịch sử thanh toán patient xem được đầy đủ

---

## 8. Phase 6 — Realtime (WebSocket + Notifications) (Tuần 12–13)

> **Mục tiêu:** Chat bác sĩ-bệnh nhân, push notification realtime

### 8.1 WebSocket Config (STOMP)

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic", "/queue"); // Message broker
    registry.setApplicationDestinationPrefixes("/app"); // Client gửi lên
    registry.setUserDestinationPrefix("/user");         // User-specific channel
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
            .setAllowedOriginPatterns(frontendUrl)
            .withSockJS(); // Fallback cho browser cũ
  }
}
```

### 8.2 Chat Endpoints

```java
// REST — Load lịch sử chat
GET  /api/messages/{partnerId}?page=0&size=50

// WebSocket STOMP
// Gửi tin nhắn
SEND /app/chat.send
Body: { toUserId, content, type }

// Nhận tin nhắn (subscribe)
SUBSCRIBE /user/queue/messages
→ Receive: { id, fromId, toId, content, sentAt, isRead }

// Typing indicator
SEND /app/chat.typing → { toUserId, isTyping }
SUBSCRIBE /user/queue/typing

// Mark as read
PATCH /api/messages/{id}/read
```

### 8.3 Notification System

```java
@Service
public class NotificationService {

  // Gửi realtime qua WebSocket
  public void pushNotification(UUID userId, NotificationDto notification) {
    messagingTemplate.convertAndSendToUser(
        userId.toString(),
        "/queue/notifications",
        notification
    );
  }

  // Lưu vào DB cho lịch sử
  public void saveAndPush(UUID userId, NotificationType type, String title, String body) {
    Notification saved = notificationRepo.save(new Notification(...));
    pushNotification(userId, NotificationDto.from(saved));
  }
}

// Tự động trigger notification
// Khi đặt lịch thành công → notify Staff + Patient
// Khi Staff xác nhận → notify Patient
// Khi có kết quả xét nghiệm → notify Patient
// Nhắc lịch 24h trước → @Scheduled cron job
```

### 8.4 Notification Endpoints

| Method | Endpoint | Role | Mô tả |
|---|---|---|---|
| GET | `/api/notifications` | Auth | Danh sách (có unread count) |
| PATCH | `/api/notifications/{id}/read` | Auth | Đánh dấu đã đọc |
| PATCH | `/api/notifications/read-all` | Auth | Đọc tất cả |
| DELETE | `/api/notifications/{id}` | Auth | Xóa |

### 8.5 Scheduled Jobs

```java
@Component
public class ScheduledJobs {

  // Nhắc lịch 24h trước
  @Scheduled(cron = "0 0 8 * * *")   // 8 giờ sáng mỗi ngày
  public void sendDayBeforeReminders() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    List<Appointment> appointments = appointmentRepo.findConfirmedOnDate(tomorrow);
    appointments.forEach(apt -> {
      notificationService.saveAndPush(apt.getPatientId(), APPOINTMENT_REMINDER, ...);
      smsService.send(apt.getPatient().getPhone(), "Nhắc lịch khám ngày mai...");
    });
  }

  // Nhắc lịch 1h trước
  @Scheduled(cron = "0 0 * * * *")   // Mỗi giờ
  public void sendHourBeforeReminders() { ... }

  // Auto no-show: quá giờ 30p mà chưa check-in
  @Scheduled(cron = "0 */15 * * * *") // Mỗi 15 phút
  public void markNoShow() { ... }

  // Cảnh báo thuốc sắp hết / hết hạn
  @Scheduled(cron = "0 0 7 * * *")
  public void checkMedicineAlerts() { ... }
}
```

### 8.6 Deliverables Phase 6

- [ ] WebSocket kết nối được từ Frontend
- [ ] Chat gửi/nhận realtime giữa doctor và patient
- [ ] Typing indicator hoạt động
- [ ] Push notification khi đặt lịch, xác nhận, kết quả XN
- [ ] Scheduled reminder 24h và 1h hoạt động
- [ ] Unread count update realtime trên Header bell icon

---

## 9. Phase 7 — Admin & Reports (Tuần 14–15)

> **Mục tiêu:** Admin quản lý toàn hệ thống, báo cáo thống kê

### 9.1 User Management

| Method | Endpoint | Role | Mô tả |
|---|---|---|---|
| GET | `/api/admin/users` | Admin | Danh sách users (filter, paginate) |
| GET | `/api/admin/users/{id}` | Admin | Chi tiết user |
| POST | `/api/admin/users` | Admin | Tạo user (doctor/staff) |
| PUT | `/api/admin/users/{id}` | Admin | Cập nhật |
| PATCH | `/api/admin/users/{id}/block` | Admin | Khóa tài khoản |
| PATCH | `/api/admin/users/{id}/unblock` | Admin | Mở khóa |
| DELETE | `/api/admin/users/{id}` | Admin | Xóa (soft delete) |
| PATCH | `/api/admin/doctors/{id}/approve` | Admin | Duyệt bác sĩ mới |

### 9.2 Review Management

| Method | Endpoint | Role | Mô tả |
|---|---|---|---|
| GET | `/api/admin/reviews?status=pending` | Admin | Đánh giá chờ duyệt |
| PATCH | `/api/admin/reviews/{id}/approve` | Admin | Phê duyệt |
| PATCH | `/api/admin/reviews/{id}/reject` | Admin | Từ chối + lý do |
| POST | `/api/patient/reviews` | Patient | Bệnh nhân gửi đánh giá |

### 9.3 Service & Medicine Management

```
Services:  GET|POST|PUT|DELETE /api/admin/services
Medicines: GET|POST|PUT|DELETE /api/admin/medicines
           PATCH /api/admin/medicines/{id}/restock
```

### 9.4 Reports & Analytics

```
GET /api/admin/reports/revenue?from=&to=&groupBy=day|week|month
GET /api/admin/reports/appointments?from=&to=
GET /api/admin/reports/doctors/performance
GET /api/admin/reports/patients/growth
GET /api/admin/reports/specialties/popularity
GET /api/admin/reports/medicines/inventory
```

**Query ví dụ — Revenue theo tháng:**

```sql
SELECT
  DATE_TRUNC('month', i.paid_at) AS month,
  SUM(i.total_amount) AS revenue,
  COUNT(i.id) AS invoice_count
FROM invoices i
WHERE i.status = 'paid'
  AND i.paid_at BETWEEN :from AND :to
GROUP BY DATE_TRUNC('month', i.paid_at)
ORDER BY month;
```

### 9.5 Audit Log

Mọi thao tác quan trọng đều ghi `audit_logs`:

```java
@Aspect
@Component
public class AuditLogAspect {
  @AfterReturning("@annotation(Auditable)")
  public void log(JoinPoint jp, Object result) {
    // Ghi: actor, action, entityType, entityId, oldValue, newValue, ipAddress
    auditLogRepo.save(new AuditLog(...));
  }
}
```

### 9.6 Deliverables Phase 7

- [ ] Admin CRUD users, block/unblock hoạt động
- [ ] Review approval workflow hoàn chỉnh
- [ ] Báo cáo doanh thu theo tháng đúng số liệu
- [ ] Báo cáo lịch hẹn theo trạng thái
- [ ] Inventory thuốc với cảnh báo tự động
- [ ] Audit log ghi lại mọi action quan trọng

---

## 10. Phase 8 — Security, Testing & Deploy (Tuần 16–17)

### 10.1 Security Hardening

```
□ HTTPS bắt buộc (redirect HTTP → HTTPS)
□ CORS whitelist chỉ frontend domain
□ Helmet headers (X-Content-Type-Options, X-Frame-Options, ...)
□ Rate limiting: 100 req/phút/IP (global), 5 req/phút (auth endpoints)
□ Input validation: Zod trên FE + Bean Validation trên BE (double validation)
□ SQL Injection: JPA parameterized queries (Hibernate xử lý tự động)
□ XSS: Sanitize HTML input (jsoup) nếu cho phép rich text
□ JWT signature verification + expiry check
□ Sensitive data không log (password, token, số thẻ)
□ File upload validation: type whitelist + size limit + virus scan
□ Password policy: min 8 ký tự, có số + chữ hoa + ký tự đặc biệt
```

### 10.2 Testing Strategy

```
Unit Tests (JUnit 5 + Mockito):
  - Service layer: business logic, edge cases
  - Utility classes: SlotGenerator, PdfGenerator

Integration Tests (Spring Boot Test + Testcontainers):
  - Repository layer: custom queries
  - Controller layer: full HTTP flow với mock security

E2E Key Flows:
  - Đăng ký → đăng nhập → đặt lịch → check-in → hoàn thành → thanh toán
  - Double booking prevention
  - JWT refresh và logout
  - VNPay callback flow (mock)

Coverage Target: ≥ 70% service layer
```

```java
// Testcontainers — dùng PostgreSQL thật trong test
@SpringBootTest
@Testcontainers
class AppointmentServiceTest {
  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15-alpine");

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", postgres::getJdbcUrl);
  }

  @Test
  void shouldPreventDoubleBooking() { ... }
}
```

### 10.3 Deploy

**Dockerfile:**

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/medcare-backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=prod"]
```

**CI/CD (GitHub Actions):**

```yaml
on: [push to main]
jobs:
  build-test-deploy:
    steps:
      - Test: mvn test
      - Build: mvn package -DskipTests
      - Docker build & push → Docker Hub
      - Deploy → Railway / Render
```

### 10.4 Deliverables Phase 8

- [ ] Unit tests cho toàn bộ service layer
- [ ] Integration tests cho booking flow
- [ ] Security audit passed (OWASP Top 10 check)
- [ ] Docker image build thành công
- [ ] CI/CD pipeline chạy tự động
- [ ] Môi trường Production lên live
- [ ] `GET /actuator/health` trả UP trên prod

---

## 11. API Endpoint Map — Toàn bộ

Map đầy đủ từ 33 Frontend routes → Backend API:

| Frontend Route | Component | Backend Endpoints cần thiết |
|---|---|---|
| `/` | LandingPage | `GET /api/doctors`, `GET /api/specialties` |
| `/booking` | BookingPage | `GET /api/doctors/{id}/slots`, `POST /api/appointments` |
| `/doctors` | DoctorsPage | `GET /api/doctors?{filters}`, `GET /api/specialties` |
| `/search` | SearchResults | `GET /api/search?q=` |
| `/login` | AuthPage | `POST /api/auth/login`, `POST /api/auth/register` |
| `/patient` | PatientDashboard | `GET /api/patient/appointments`, `GET /api/patient/invoices` |
| `/patient/profile` | PatientProfile | `GET/PUT /api/patient/profile` |
| `/patient/history` | PatientHistory | `GET /api/patient/medical-records` |
| `/patient/prescriptions` | PatientPrescriptions | `GET /api/patient/prescriptions` |
| `/patient/payments` | PatientPayments | `GET /api/patient/invoices` |
| `/patient/medical-records/:id` | MedicalRecordDetail | `GET /api/medical-records/{id}` |
| `/patient/prescriptions/:id` | PrescriptionDetail | `GET /api/prescriptions/{id}` |
| `/patient/lab-results/:id` | LabTestResults | `GET /api/lab-tests/{id}` |
| `/patient/review/:appointmentId` | ReviewDoctor | `POST /api/reviews` |
| `/patient/payment/:invoiceId` | Payment | `POST /api/payments/vnpay/create` |
| `/doctor` | DoctorDashboard | `GET /api/appointments/today`, `GET /api/messages` |
| `/doctor/schedule` | DoctorSchedule | `GET/POST /api/doctor/schedule` |
| `/doctor/profile` | DoctorProfile | `GET/PUT /api/doctor/profile` |
| `/doctor/patients` | DoctorPatients | `GET /api/doctor/patients` |
| `/doctor/records` | DoctorRecords | `GET /api/doctor/records` |
| `/doctor/medical-record/:id` | MedicalRecordForm | `POST /api/medical-records` |
| `/admin` | AdminDashboard | `GET /api/admin/stats`, `GET /api/admin/reports/*` |
| `/admin/users` | UserManagement | `GET/POST/PUT/DELETE /api/admin/users` |
| `/admin/services` | ServiceManagement | `GET/POST/PUT/DELETE /api/admin/services` |
| `/admin/medicine` | MedicineInventory | `GET/POST/PUT/DELETE /api/admin/medicines` |
| `/admin/reports` | ReportsAnalytics | `GET /api/admin/reports/*` |
| `/admin/reviews` | ReviewApproval | `GET /api/admin/reviews`, `PATCH .../approve` |
| `/staff` | StaffDashboard | `GET /api/appointments/today`, `GET /api/appointments/queue` |
| `/staff/checkin` | CheckInQueue | `PATCH /api/appointments/{id}/checkin` |
| `/staff/invoices` | StaffInvoices | `GET /api/staff/invoices` |
| `/staff/invoice/:id` | InvoiceCreation | `POST /api/invoices`, `POST /api/payments/cash` |
| `/settings` | Settings | `PUT /api/auth/change-password`, `PUT /api/user/settings` |

---

## 12. Entity Map — 24 Tables → JPA

| SQL Table | JPA Entity | Quan hệ chính |
|---|---|---|
| `users` | `User` | OneToOne → Patient, Doctor |
| `patients` | `Patient` | ManyToOne → User; OneToMany → Appointment |
| `doctors` | `Doctor` | ManyToOne → User, Specialty; OneToMany → DoctorSchedule |
| `specialties` | `Specialty` | OneToMany → Doctor |
| `doctor_schedules` | `DoctorSchedule` | ManyToOne → Doctor |
| `doctor_leaves` | `DoctorLeave` | ManyToOne → Doctor |
| `doctor_certifications` | `DoctorCertification` | ManyToOne → Doctor |
| `appointments` | `Appointment` | ManyToOne → Patient, Doctor, Service |
| `appointment_reminders` | `AppointmentReminder` | ManyToOne → Appointment |
| `medical_records` | `MedicalRecord` | OneToOne → Appointment |
| `prescriptions` | `Prescription` | ManyToOne → MedicalRecord |
| `prescription_items` | `PrescriptionItem` | ManyToOne → Prescription, Medicine |
| `lab_tests` | `LabTest` | ManyToOne → MedicalRecord |
| `lab_test_items` | `LabTestItem` | ManyToOne → LabTest |
| `invoices` | `Invoice` | ManyToOne → Appointment |
| `invoice_items` | `InvoiceItem` | ManyToOne → Invoice |
| `services` | `Service` | ManyToOne → Specialty |
| `medicines` | `Medicine` | OneToMany → PrescriptionItem |
| `reviews` | `Review` | ManyToOne → Appointment, Doctor, Patient |
| `review_reports` | `ReviewReport` | ManyToOne → Review, User |
| `notifications` | `Notification` | ManyToOne → User |
| `messages` | `Message` | ManyToOne → Patient, Doctor |
| `audit_logs` | `AuditLog` | ManyToOne → User |
| `password_reset_tokens` | — | Quản lý bằng Redis (không cần entity) |

---

## 13. Kết nối Frontend (SWR → API)

Sau khi backend sẵn sàng, thay mock data từng bước:

### Bước 1: Tạo API client

```typescript
// src/app/lib/api.ts
const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api';

export const api = {
  get: (url: string) =>
    fetch(`${BASE_URL}${url}`, {
      headers: { Authorization: `Bearer ${getAccessToken()}` },
    }).then(handleResponse),

  post: (url: string, body: unknown) =>
    fetch(`${BASE_URL}${url}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${getAccessToken()}`,
      },
      body: JSON.stringify(body),
    }).then(handleResponse),
};
```

### Bước 2: Update SWR hooks

```typescript
// Hiện tại — mock
// src/app/hooks/useAppointments.ts
import { patientAppointments } from '../data/mockData';
export function useAppointments() {
  return { data: patientAppointments, isLoading: false };
}

// Sau khi có backend — real API
export function useAppointments() {
  return useSWR('/patient/appointments', (url) => api.get(url));
}

// SWR Mutation — cancel appointment
export function useCancelAppointment() {
  return useSWRMutation(
    '/appointments',
    (url, { arg: id }: { arg: string }) => api.patch(`${url}/${id}/cancel`)
  );
}
```

### Bước 3: Update useAuth

```typescript
// src/app/hooks/useAuth.ts
export function useAuth() {
  const { data: user, mutate } = useSWR('/auth/me', api.get, {
    revalidateOnFocus: false,
    shouldRetryOnError: false,
  });

  const login = async (email: string, password: string) => {
    const res = await api.post('/auth/login', { email, password });
    setTokens(res.data.accessToken, res.data.refreshToken);
    mutate(); // Revalidate user
  };

  const logout = async () => {
    await api.post('/auth/logout', { refreshToken: getRefreshToken() });
    clearTokens();
    mutate(null, false);
  };

  return { user, login, logout, isLoading: !user };
}
```

### Thứ tự thay thế mock data

```
Tuần 3-4:  useAuth → /api/auth/*
Tuần 5-7:  useDoctors, useSlots, useAppointments → Booking flow
Tuần 8-9:  useMedicalRecords, usePrescriptions → Patient history
Tuần 10-11: useInvoices → Payment flow
Tuần 12-13: useNotifications, useMessages → WebSocket real
Tuần 14-15: Admin hooks → Reports & Management
```

---

## 14. Timeline Tổng

| Phase | Nội dung | Thời gian | Deliverable chính |
|---|---|---|---|
| **Phase 1** | Foundation, DB, Docker | Tuần 1–2 | Health check, Flyway migration |
| **Phase 2** | Auth, JWT, 5 roles | Tuần 3–4 | Login/Register hoạt động |
| **Phase 3** | Booking Flow cốt lõi | Tuần 5–7 | Đặt lịch end-to-end |
| **Phase 4** | Medical Records, Prescriptions | Tuần 8–9 | Bệnh án + đơn thuốc PDF |
| **Phase 5** | Payment (VNPay + MoMo) | Tuần 10–11 | Thanh toán thật |
| **Phase 6** | Realtime (WebSocket) | Tuần 12–13 | Chat + push notification |
| **Phase 7** | Admin + Reports | Tuần 14–15 | Dashboard analytics |
| **Phase 8** | Security, Testing, Deploy | Tuần 16–17 | Production live |
| **Tổng** | | **~17 tuần** | Full stack hoàn chỉnh |

> **Tip:** Chạy song song Frontend mock và Backend API bằng cách dùng biến môi trường `VITE_USE_MOCK=true` để FE vẫn dùng mock trong khi test. Khi Backend sẵn sàng từng module, flip flag về `false`.

---

*Tài liệu này nên được cập nhật song song với `MEDCARE_FLOW.md` khi triển khai từng phase.*
