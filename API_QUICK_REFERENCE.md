# Doctor Schedule Management API - Quick Reference

## 5.5 Doctor Schedule Management

| Method | Endpoint | Role | Request | Response | Status | Description |
|---|---|---|---|---|---|---|
| GET | `/api/v1/doctor/schedule` | Doctor | — | `[{schedule}]` | 200 | Lịch làm việc của mình |
| POST | `/api/v1/doctor/schedule` | Doctor | `{schedule}` | `{schedule}` | 201 | Thêm ca làm việc |
| DELETE | `/api/v1/doctor/schedule/{id}` | Doctor | — | 204 | 204 | Xóa ca |
| POST | `/api/v1/doctor/leaves` | Doctor | `{leaveRequest}` | `{leave}` | 201 | Đăng ký nghỉ phép |
| GET | `/api/v1/doctor/leaves` | Doctor/Admin | — | `[{leave}]` | 200 | Danh sách nghỉ phép |
| PATCH | `/api/v1/doctor/leaves/{id}/approve` | Admin | `{approveRequest}` | `{leave}` | 200 | Duyệt nghỉ phép |

---

## Request/Response Schemas

### Schedule Object
```json
{
  "id": "uuid",
  "dayOfWeek": 1,
  "startTime": "08:00:00",
  "endTime": "17:00:00",
  "slotDuration": 30,
  "maxPatientsPerSlot": 1,
  "location": "Room 101",
  "isActive": true
}
```

### Leave Object
```json
{
  "id": "uuid",
  "leaveDate": "25/12/2024",
  "startTime": "08:00:00",
  "endTime": "17:00:00",
  "reason": "Personal reasons",
  "status": "pending",
  "doctorName": "Dr. John Doe",
  "doctorId": "uuid"
}
```

### Schedule Request Body
```json
{
  "dayOfWeek": 1,
  "startTime": "08:00:00",
  "endTime": "17:00:00",
  "slotDuration": 30,
  "maxPatientsPerSlot": 1,
  "location": "Room 101",
  "isActive": true
}
```

### Leave Request Body
```json
{
  "leaveDate": "25/12/2024",
  "startTime": "08:00:00",
  "endTime": "17:00:00",
  "reason": "Personal reasons"
}
```

### Approve Request Body
```json
{
  "status": "approved",
  "rejectionReason": "Optional reason if rejected"
}
```

---

## Detailed Endpoint Documentation

### 1. Get Doctor Schedule
```
GET /api/v1/doctor/schedule
Authorization: Bearer <token>
```

**Success Response:**
```
Status: 200 OK
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "dayOfWeek": 1,
    "startTime": "08:00:00",
    "endTime": "17:00:00",
    "slotDuration": 30,
    "maxPatientsPerSlot": 1,
    "location": "Room 101",
    "isActive": true
  }
]
```

**Error Responses:**
```
Status: 401 Unauthorized
{ "error": "User not authenticated" }

Status: 404 Not Found
{ "error": "Doctor not found" }

Status: 500 Internal Server Error
{ "error": "Failed to get schedules", "message": "..." }
```

---

### 2. Add Doctor Schedule
```
POST /api/v1/doctor/schedule
Authorization: Bearer <token>
Content-Type: application/json

{
  "dayOfWeek": 1,
  "startTime": "08:00:00",
  "endTime": "17:00:00",
  "slotDuration": 30,
  "maxPatientsPerSlot": 1,
  "location": "Room 101",
  "isActive": true
}
```

**Success Response:**
```
Status: 201 Created
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "dayOfWeek": 1,
  "startTime": "08:00:00",
  "endTime": "17:00:00",
  "slotDuration": 30,
  "maxPatientsPerSlot": 1,
  "location": "Room 101",
  "isActive": true
}
```

**Validation Error:**
```
Status: 400 Bad Request
{
  "error": "Validation failed",
  "message": "Day of week must be between 0-6"
}
```

---

### 3. Delete Doctor Schedule
```
DELETE /api/v1/doctor/schedule/{id}
Authorization: Bearer <token>
```

**Success Response:**
```
Status: 204 No Content
```

**Error Responses:**
```
Status: 400 Bad Request
{ "error": "Invalid schedule ID format" }

Status: 400 Bad Request
{ "error": "Schedule not found" }

Status: 400 Bad Request
{ "error": "You can only delete your own schedules" }
```

---

### 4. Request Doctor Leave
```
POST /api/v1/doctor/leaves
Authorization: Bearer <token>
Content-Type: application/json

{
  "leaveDate": "25/12/2024",
  "startTime": "08:00:00",
  "endTime": "17:00:00",
  "reason": "Personal reasons"
}
```

**Success Response:**
```
Status: 201 Created
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "leaveDate": "25/12/2024",
  "startTime": "08:00:00",
  "endTime": "17:00:00",
  "reason": "Personal reasons",
  "status": "pending",
  "doctorName": "Dr. John Doe",
  "doctorId": "550e8400-e29b-41d4-a716-446655440001"
}
```

**Validation Error:**
```
Status: 400 Bad Request
{
  "error": "Validation failed",
  "message": "Reason is required"
}
```

---

### 5. Get Doctor Leaves

#### For Doctor:
```
GET /api/v1/doctor/leaves
Authorization: Bearer <token>
```

#### For Admin (with optional filter):
```
GET /api/v1/doctor/leaves?doctorId=<doctor-uuid>
Authorization: Bearer <admin_token>
```

**Success Response:**
```
Status: 200 OK
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "leaveDate": "25/12/2024",
    "startTime": "08:00:00",
    "endTime": "17:00:00",
    "reason": "Personal reasons",
    "status": "pending",
    "doctorName": "Dr. John Doe",
    "doctorId": "550e8400-e29b-41d4-a716-446655440001"
  }
]
```

**Admin - Get all pending leaves:**
```
GET /api/v1/doctor/leaves
Authorization: Bearer <admin_token>

Status: 200 OK
[
  { ... pending leaves from all doctors ... }
]
```

---

### 6. Approve/Reject Doctor Leave
```
PATCH /api/v1/doctor/leaves/{id}/approve
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "status": "approved"
}
```

Or to reject:
```json
{
  "status": "rejected",
  "rejectionReason": "Insufficient notice period"
}
```

**Success Response:**
```
Status: 200 OK
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "leaveDate": "25/12/2024",
  "startTime": "08:00:00",
  "endTime": "17:00:00",
  "reason": "Personal reasons",
  "status": "approved",
  "doctorName": "Dr. John Doe",
  "doctorId": "550e8400-e29b-41d4-a716-446655440001"
}
```

**Authorization Error:**
```
Status: 400 Bad Request
{ "error": "Only admins can approve leaves" }

Status: 400 Bad Request
{ "error": "Invalid status. Must be 'approved' or 'rejected'" }
```

---

## HTTP Status Codes

| Code | Meaning | Example |
|---|---|---|
| 200 | OK | Get leaves successful |
| 201 | Created | Schedule/Leave created |
| 204 | No Content | Delete successful |
| 400 | Bad Request | Invalid input, validation failed |
| 401 | Unauthorized | Missing or invalid token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 500 | Server Error | Internal server error |

---

## Field Validation Rules

### Schedule Fields
| Field | Type | Required | Rules |
|---|---|---|---|
| dayOfWeek | Integer | Yes | 0-6 (0=Sun, 6=Sat) |
| startTime | Time | Yes | HH:mm:ss format |
| endTime | Time | Yes | HH:mm:ss format |
| slotDuration | Integer | No | > 0, default: 30 |
| maxPatientsPerSlot | Integer | No | > 0, default: 1 |
| location | String | No | Max 255 chars |
| isActive | Boolean | No | Default: true |

### Leave Fields
| Field | Type | Required | Rules |
|---|---|---|---|
| leaveDate | Date | Yes | dd/MM/yyyy format |
| startTime | Time | No | HH:mm:ss format (optional for partial leave) |
| endTime | Time | No | HH:mm:ss format (optional for partial leave) |
| reason | String | Yes | Non-empty |
| status | String | No | pending/approved/rejected |

---

## Date/Time Formats

- **Date Format**: `dd/MM/yyyy` (e.g., `25/12/2024`)
- **Time Format**: `HH:mm:ss` (e.g., `14:30:00`)
- **Timezone**: Asia/Ho_Chi_Minh (UTC+7)

---

## Authentication

All protected endpoints require Bearer token in Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Token can be obtained from:
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`

---

## CORS Configuration

Allowed Origins:
- http://localhost:3000
- http://localhost:4200
- http://localhost:5173

Allowed Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
Allowed Headers: Authorization, Content-Type, Accept, etc.

---

## Implementation Notes

1. **Database**: Uses PostgreSQL with Flyway migrations
2. **Framework**: Spring Boot 3.5.13 with Apache Camel
3. **Authentication**: JWT tokens with RS256 signature
4. **Validation**: Uses Jakarta Validation API
5. **Soft Delete**: Records have `deletedAt` timestamp
6. **Transactions**: All operations are transactional

---

