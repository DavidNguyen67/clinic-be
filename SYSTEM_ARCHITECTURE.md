# Doctor Schedule Management - Architecture & System Design

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Client Application                          │
│                    (Web/Mobile/Desktop Client)                      │
└────────────────────────────────┬──────────────────────────────────────┘
                                 │
                    HTTP/REST with JWT Token
                                 │
┌────────────────────────────────▼──────────────────────────────────────┐
│                      Spring Security (CORS)                          │
│                    JWT Authentication Filter                         │
└────────────────────────────────┬──────────────────────────────────────┘
                                 │
┌────────────────────────────────▼──────────────────────────────────────┐
│                         REST Endpoints                               │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │  /api/v1/doctor/schedule                                    │   │
│  │  /api/v1/doctor/leaves                                      │   │
│  └──────────────────────────────────────────────────────────────┘   │
└────────────────────────────────┬──────────────────────────────────────┘
                                 │
┌────────────────────────────────▼──────────────────────────────────────┐
│              Apache Camel Routing Engine (routes.yaml)               │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │  Route Processing:                                           │   │
│  │  1. Request Deserialization                                 │   │
│  │  2. Validation                                              │   │
│  │  3. Business Logic Processor                                │   │
│  │  4. Response Marshaling                                     │   │
│  └──────────────────────────────────────────────────────────────┘   │
└────────────────────────────────┬──────────────────────────────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                        │                        │
┌───────▼────────┐    ┌──────────▼──────────┐    ┌───────▼──────────┐
│  Processors    │    │   Service Layer    │    │  Exception       │
│  (6 classes)   │    │  (Business Logic)  │    │  Handling        │
│                │    │                    │    │                  │
│ GetSchedule    │    │ DoctorServiceInv   │    │ BadRequestEx     │
│ AddSchedule    │    │ (Impl methods)     │    │ NotFoundEx       │
│ DeleteSchedule │    │                    │    │ UnauthorizedEx   │
│ RequestLeave   │    │ Role-based Logic   │    │                  │
│ GetLeaves      │    │ Authorization      │    └──────────────────┘
│ ApproveLeave   │    │ Checks             │
│                │    │                    │
└────────────────┘    └──────────┬─────────┘
                                 │
┌────────────────────────────────▼──────────────────────────────────────┐
│                   Repository Layer (Data Access)                      │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │  DoctorScheduleRepository                                    │   │
│  │  ├─ findByDoctorId()                                        │   │
│  │  ├─ findActiveByDoctorId()                                  │   │
│  │  └─ countByDoctorIdAndDayOfWeek()                          │   │
│  │                                                              │   │
│  │  DoctorLeaveRepository                                       │   │
│  │  ├─ findByDoctorId()                                        │   │
│  │  ├─ findByStatus()                                          │   │
│  │  ├─ findPendingByDoctorId()                                │   │
│  │  └─ findByDoctorIdAndLeaveDate()                           │   │
│  └──────────────────────────────────────────────────────────────┘   │
└────────────────────────────────┬──────────────────────────────────────┘
                                 │
┌────────────────────────────────▼──────────────────────────────────────┐
│                   PostgreSQL Database                                 │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │  Tables:                                                     │   │
│  │  • doctor_schedules (day, time, duration, location)         │   │
│  │  • doctor_leave (leave_date, reason, status)                │   │
│  │  • doctors (doctor details)                                 │   │
│  │  • users (doctor/admin info)                                │   │
│  │                                                              │   │
│  │  Indexes:                                                    │   │
│  │  • idx_doctor_schedules_doctor_id                           │   │
│  │  • idx_doctor_leave_doctor_id                               │   │
│  │  • idx_doctor_leave_status                                  │   │
│  └──────────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 📦 Component Interaction Diagram

```
Request Flow:
────────────

Client
   │
   │ POST /api/v1/doctor/schedule with Bearer Token
   ▼
HTTP Layer
   │
   │ ✓ Check Authorization Header
   │ ✓ Extract JWT Token
   │ ✓ Validate Token Signature
   ▼
Spring Security
   │
   │ ✓ CORS Check
   │ ✓ Role Validation
   │ ✓ Set SecurityContext
   ▼
REST Endpoint Mapping
   │
   │ URL: /v1/doctor/schedule (POST)
   │ Route: direct:doctor-add-schedule
   ▼
Camel Route Processing
   │
   ├─ Step 1: SetHeader (DTO Class)
   │         X-DTO-Class = DoctorScheduleRequestDTO
   │
   ├─ Step 2: RequestDeserializerProcessor
   │         Parse JSON to POJO
   │
   ├─ Step 3: ValidationProcessor
   │         Validate all fields
   │         Check business rules
   │
   ├─ Step 4: AddDoctorScheduleProcessor
   │         Call service layer
   │
   └─ Step 5: Response Marshaling
            Convert to JSON
            Wrap with API Response
   ▼
Service Layer
   │
   ├─ getCurrentUser()
   │  └─ Get from SecurityContext
   │     └─ Find User in DB
   │
   ├─ Validate dayOfWeek (0-6)
   │
   ├─ Get Doctor entity
   │
   ├─ Create DoctorSchedule entity
   │
   └─ Call Repository
   ▼
Repository Layer
   │
   └─ doctorScheduleRepository.save()
      └─ Execute INSERT SQL
   ▼
PostgreSQL
   │
   └─ Insert into doctor_schedules table
      ✓ Set created_at timestamp
      ✓ Generate UUID
      ✓ Return saved entity
   ▼
Back Through Stack
   │
   ├─ Repository returns entity
   ├─ Service converts to DTO
   ├─ Processor returns entity
   ├─ Camel marshals to JSON
   ├─ Spring sets 201 status
   ▼
Client Receives
   HTTP/1.1 201 Created
   Content-Type: application/json
   CORS Headers
   
   {
     "id": "uuid",
     "dayOfWeek": 1,
     ...
   }
```

---

## 🔐 Security Flow

```
┌──────────────────────────────────────────────┐
│         Client Request with Token           │
│  Authorization: Bearer eyJhb...              │
└─────────────────┬──────────────────────────┘
                  │
┌─────────────────▼──────────────────────────┐
│    JwtAuthenticationFilter                 │
│  (Spring Security Filter Chain)            │
│  1. Extract token from header              │
│  2. Verify signature (RS256)               │
│  3. Check expiration                       │
│  4. Set SecurityContext                    │
└─────────────────┬──────────────────────────┘
                  │
          ┌───────▼────────┐
          │   Valid?       │
          └───┬────────┬───┘
              │        │
         YES  │        │  NO
             │        └────→ 401 Unauthorized
             │
┌────────────▼─────────────────────────────┐
│    CORS Check                            │
│  Check Origin in allowed-origins         │
│  Add CORS headers to response            │
└────────────┬─────────────────────────────┘
             │
┌────────────▼─────────────────────────────┐
│    Endpoint Authorization Check          │
│  Is /api/v1/doctor/** authenticated?    │
│  - YES → Proceed                         │
│  - NO → 403 Forbidden                    │
└────────────┬─────────────────────────────┘
             │
┌────────────▼─────────────────────────────┐
│    Service Layer Authorization           │
│  - Doctor: Can only access own data      │
│  - Admin: Can access all data            │
└────────────┬─────────────────────────────┘
             │
             ▼
         Proceed with Request
```

---

## 📊 Database Schema Relationships

```
┌─────────────┐
│   users     │
├─────────────┤
│ id (PK)     │◄─────────────┐
│ email       │              │
│ full_name   │              │
│ role        │              │
└─────────────┘              │
        ▲                    │
        │                    │
        │ 1:1                │ 1:1
        │                    │
┌───────┴────────┐      ┌────┴──────────┐
│    doctors     │      │    patients    │
├────────────────┤      ├────────────────┤
│ id (PK)        │      │ id (PK)        │
│ user_id (FK)   │      │ user_id (FK)   │
│ specialty_id   │      │ patient_code   │
└────┬───────────┘      └────────────────┘
     │
     │ 1:M
     │
     ├─────────┬──────────┬────────────┐
     │         │          │            │
┌────▼──────────┐  ┌──────▼──┐  ┌────▼─────┐
│ doctor_       │  │ doctor_ │  │appointments
│ schedules     │  │ leave   │  └──────────┘
├───────────────┤  ├─────────┤
│ id (PK)       │  │ id (PK) │
│ doctor_id(FK) │  │doctor_id│
│ day_of_week   │  │(FK)     │
│ start_time    │  │leave_   │
│ end_time      │  │date     │
│ slot_duration │  │reason   │
│ location      │  │status   │
│ is_active     │  └─────────┘
└───────────────┘
```

---

## 🔄 Request Processing Sequence

### Add Schedule Request

```
1. Client
   ├─ Generate JWT token (login)
   ├─ Prepare request body (JSON)
   └─ POST /api/v1/doctor/schedule

2. REST Layer
   ├─ Receive request
   ├─ Extract Authorization header
   └─ Route to Camel

3. Camel Route
   ├─ Extract request body
   ├─ Set DTO class header
   ├─ Deserialize JSON
   ├─ Validate fields
   ├─ Call processor
   └─ Return to client

4. Processor Layer
   └─ Call DoctorServiceImp.addDoctorSchedule()

5. Service Layer
   ├─ Get current user from SecurityContext
   ├─ Find doctor by user ID
   ├─ Validate dayOfWeek
   ├─ Create DoctorSchedule entity
   ├─ Call repository.save()
   ├─ Convert to DTO
   └─ Return ResponseEntity

6. Database
   ├─ Execute INSERT
   ├─ Generate UUID
   ├─ Set timestamps
   └─ Return saved entity

7. Response
   ├─ Marshal to JSON
   ├─ Add CORS headers
   ├─ Set 201 Created status
   └─ Send to client
```

---

## 🔍 Authorization Logic

```
Is User Authenticated?
  │
  ├─ YES → Continue
  └─ NO → 401 Unauthorized

Can User Access /api/v1/doctor/**?
  │
  ├─ YES → Continue
  └─ NO → 403 Forbidden

Is User a Doctor?
  │
  ├─ YES → Can manage own data
  │        Only see own schedules/leaves
  │
  └─ NO (Admin)
       Can manage all data
       Can approve leaves

Specific Endpoint Checks:
├─ GET /schedule → Doctor: own, Admin: all
├─ POST /schedule → Doctor only
├─ DELETE /schedule/{id} → Doctor: own
├─ POST /leaves → Doctor only
├─ GET /leaves → Doctor: own, Admin: all
└─ PATCH /leaves/{id}/approve → Admin only
```

---

## 📈 Data Flow Diagram

```
Input Data
    │
    ▼
┌─────────────────────┐
│ RequestDeserializer │ → Parse JSON to DTO
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│ Validation          │ → Validate all fields
│ - @NotNull          │   Check business rules
│ - @NotBlank         │   Day of week 0-6
│ - @Positive         │   Date formats
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│ SecurityContext     │ → Get current user
│ Extraction          │   Extract email
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│ Database Query      │ → Find doctor/user
│ & Authorization    │   Check permissions
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│ Business Logic      │ → Create entity
│ Processing          │   Save to DB
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│ DTO Conversion      │ → Map entity to DTO
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│ Response            │ → Marshal to JSON
│ Marshaling          │   Wrap response
└─────────────────────┘
    │
    ▼
Output Response
```

---

## 🛡️ Error Handling Flow

```
Exception Occurs
    │
    ▼
┌─────────────────────────────┐
│ Is it a known exception?    │
├─────────────────────────────┤
│ ✓ BadRequestException       │
│ ✓ NotFoundException         │
│ ✓ UnauthorizedException     │
│ ✓ ValidationException       │
└──┬──────────────────────────┘
   │
   ├─ YES → Map to HTTP Status
   │        Create error response
   │        Log with details
   │
   └─ NO → 500 Internal Server Error
           Log full exception
           Return generic message
   │
   ▼
┌─────────────────────────────┐
│ Format Error Response       │
├─────────────────────────────┤
│ {                           │
│   "error": "Message",       │
│   "message": "Details"      │
│ }                           │
└─────────────────────────────┘
   │
   ▼
HTTP Response
(400/401/403/404/500 + JSON body)
```

---

## 🎯 Implementation Summary

| Component | Layer | Responsibility |
|-----------|-------|-----------------|
| REST Endpoints | API | Route requests to Camel |
| Camel Routes | Integration | Orchestrate processing pipeline |
| Processors | API | Call service methods |
| Services | Business Logic | Core functionality & validation |
| Repositories | Data Access | Database operations |
| Entities | Domain | Data models |
| DTOs | Transfer | Request/response objects |
| Security | Framework | Authentication & authorization |

---

## 📊 Deployment Architecture

```
┌─────────────────────────────────────┐
│   Load Balancer / Reverse Proxy     │
│   (nginx / Apache)                  │
└────────────────┬────────────────────┘
                 │
    ┌────────────┼────────────┐
    │            │            │
┌───▼──┐    ┌───▼──┐    ┌───▼──┐
│Spring│    │Spring│    │Spring│
│Boot  │    │Boot  │    │Boot  │
│App 1 │    │App 2 │    │App N │
└───┬──┘    └───┬──┘    └───┬──┘
    │           │           │
    └───────────┼───────────┘
                │
        ┌───────▼────────┐
        │ PostgreSQL DB  │
        │ (Connection    │
        │  Pool)         │
        └────────────────┘
```

---

This architecture ensures:
✓ Scalability - Multiple instances possible
✓ Security - JWT authentication enforced
✓ Performance - Database indexing & connection pooling
✓ Maintainability - Clear separation of concerns
✓ Reliability - Transaction management & error handling

