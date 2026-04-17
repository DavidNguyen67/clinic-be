# Doctor Schedule Management Implementation - Summary

## What Was Added

### 1. New DTOs (Data Transfer Objects)
Created in `src/main/java/com/camel/clinic/dto/doctor/`:

- **DoctorScheduleRequestDTO.java** - Request DTO for adding schedules
- **DoctorScheduleResponseDTO.java** - Response DTO for schedule operations
- **DoctorLeaveRequestDTO.java** - Request DTO for requesting leave
- **DoctorLeaveResponseDTO.java** - Response DTO for leave operations
- **DoctorLeaveApproveRequestDTO.java** - Request DTO for approving/rejecting leaves

### 2. Repository Enhancement
Updated `DoctorScheduleRepository.java`:
- Added `findByDoctorId()` - Get all schedules for a doctor
- Added `findActiveByDoctorId()` - Get active schedules only
- Added `countByDoctorIdAndDayOfWeek()` - Count schedules for validation

Created new `DoctorLeaveRepository.java`:
- `findByDoctorId()` - Get doctor's leave requests
- `findByStatus()` - Get leaves by status
- `findByDoctorIdAndLeaveDate()` - Get leaves for specific date
- `findPendingByDoctorId()` - Get pending leaves

### 3. Service Layer Implementation
Updated `DoctorService.java` interface with new method signatures
Updated `DoctorServiceImp.java` to delegate to service methods
Updated `DoctorServiceInv.java` with complete implementations:

**Doctor Schedule Methods:**
- `getDoctorSchedules()` - Retrieve doctor's schedules
- `addDoctorSchedule()` - Create new schedule
- `deleteDoctorSchedule()` - Delete schedule

**Doctor Leave Methods:**
- `requestDoctorLeave()` - Submit leave request
- `getDoctorLeaves()` - Get leaves (with role-based filtering)
- `approveDoctorLeave()` - Approve/reject leave (Admin only)

**Helper Methods:**
- `getCurrentUser()` - Extract authenticated user from security context
- `convertToScheduleDTO()` - Convert entity to DTO
- `convertToLeaveDTO()` - Convert entity to DTO

### 4. Camel Processors
Created in `src/main/java/com/camel/clinic/processor/doctor/`:

- **GetDoctorScheduleProcessor.java** - Handle GET /schedule requests
- **AddDoctorScheduleProcessor.java** - Handle POST /schedule requests
- **DeleteDoctorScheduleProcessor.java** - Handle DELETE /schedule/{id} requests
- **RequestDoctorLeaveProcessor.java** - Handle POST /leaves requests
- **GetDoctorLeavesProcessor.java** - Handle GET /leaves requests
- **ApproveDoctorLeaveProcessor.java** - Handle PATCH /leaves/{id}/approve requests

### 5. REST Configuration
Updated `src/main/resources/camel/rest.yaml`:
```yaml
- rest:
    path: /v1/doctor/schedule
    get: /schedule
    post: /schedule
    delete: /schedule/{id}

- rest:
    path: /v1/doctor/leaves
    post: /
    get: /
    patch: /{id}/approve
```

### 6. Camel Routes Configuration
Updated `src/main/resources/camel/routes.yaml` with 6 new routes:
- `doctor-get-schedule-route`
- `doctor-add-schedule-route`
- `doctor-delete-schedule-route`
- `doctor-request-leave-route`
- `doctor-get-leaves-route`
- `doctor-approve-leave-route`

### 7. Security Configuration
Updated `src/main/java/com/camel/clinic/config/SecurityConfig.java`:
- Added `/api/v1/doctor/**` to require authentication
- Maintains CORS configuration for cross-origin requests

## API Endpoints

### Doctor Schedule Management
```
GET    /api/v1/doctor/schedule              - Get my schedules (Doctor)
POST   /api/v1/doctor/schedule              - Add new schedule (Doctor)
DELETE /api/v1/doctor/schedule/{id}         - Delete schedule (Doctor)
```

### Doctor Leave Management
```
POST   /api/v1/doctor/leaves                - Request leave (Doctor)
GET    /api/v1/doctor/leaves                - Get leaves (Doctor/Admin)
PATCH  /api/v1/doctor/leaves/{id}/approve   - Approve leave (Admin)
```

## Request/Response Examples

### Add Schedule
```bash
POST /api/v1/doctor/schedule
Content-Type: application/json
Authorization: Bearer <token>

{
  "dayOfWeek": 1,
  "startTime": "08:00:00",
  "endTime": "17:00:00",
  "slotDuration": 30,
  "maxPatientsPerSlot": 1,
  "location": "Room 101",
  "isActive": true
}

Response: 201 Created
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

### Request Leave
```bash
POST /api/v1/doctor/leaves
Content-Type: application/json
Authorization: Bearer <token>

{
  "leaveDate": "25/12/2024",
  "startTime": "08:00:00",
  "endTime": "17:00:00",
  "reason": "Personal reasons"
}

Response: 201 Created
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

### Approve Leave
```bash
PATCH /api/v1/doctor/leaves/{id}/approve
Content-Type: application/json
Authorization: Bearer <admin_token>

{
  "status": "approved"
}

Response: 200 OK
{
  "id": "uuid",
  "leaveDate": "25/12/2024",
  "startTime": "08:00:00",
  "endTime": "17:00:00",
  "reason": "Personal reasons",
  "status": "approved",
  "doctorName": "Dr. John Doe",
  "doctorId": "uuid"
}
```

## Key Features

1. **Role-Based Access Control**
   - Doctors can manage their own schedules and request leaves
   - Admins can approve/reject leave requests and view all pending leaves
   - Doctors can only view their own leaves

2. **Validation**
   - Request validation using DTO with @NotNull, @NotBlank, etc.
   - Day of week validation (0-6)
   - Format validation for dates and times

3. **Error Handling**
   - Comprehensive error messages
   - Proper HTTP status codes (400, 401, 403, 404, 500)
   - Exception handling with custom exceptions

4. **Database Integration**
   - Uses existing `doctor_schedules` and `doctor_leave` tables
   - Soft delete support (deletedAt field)
   - Proper indexing for performance

5. **Security**
   - JWT authentication required
   - CORS configured
   - Authorization checks to prevent unauthorized access

6. **Timezone Support**
   - All timestamps use Asia/Ho_Chi_Minh timezone
   - Consistent date/time formatting

## Files Modified
1. `src/main/java/com/camel/clinic/service/doctor/DoctorService.java`
2. `src/main/java/com/camel/clinic/service/doctor/DoctorServiceImp.java`
3. `src/main/java/com/camel/clinic/service/doctor/DoctorServiceInv.java`
4. `src/main/java/com/camel/clinic/repository/DoctorScheduleRepository.java`
5. `src/main/resources/camel/rest.yaml`
6. `src/main/resources/camel/routes.yaml`
7. `src/main/java/com/camel/clinic/config/SecurityConfig.java`

## Files Created
1. `src/main/java/com/camel/clinic/dto/doctor/DoctorScheduleRequestDTO.java`
2. `src/main/java/com/camel/clinic/dto/doctor/DoctorScheduleResponseDTO.java`
3. `src/main/java/com/camel/clinic/dto/doctor/DoctorLeaveRequestDTO.java`
4. `src/main/java/com/camel/clinic/dto/doctor/DoctorLeaveResponseDTO.java`
5. `src/main/java/com/camel/clinic/dto/doctor/DoctorLeaveApproveRequestDTO.java`
6. `src/main/java/com/camel/clinic/repository/DoctorLeaveRepository.java`
7. `src/main/java/com/camel/clinic/processor/doctor/GetDoctorScheduleProcessor.java`
8. `src/main/java/com/camel/clinic/processor/doctor/AddDoctorScheduleProcessor.java`
9. `src/main/java/com/camel/clinic/processor/doctor/DeleteDoctorScheduleProcessor.java`
10. `src/main/java/com/camel/clinic/processor/doctor/RequestDoctorLeaveProcessor.java`
11. `src/main/java/com/camel/clinic/processor/doctor/GetDoctorLeavesProcessor.java`
12. `src/main/java/com/camel/clinic/processor/doctor/ApproveDoctorLeaveProcessor.java`
13. `DOCTOR_SCHEDULE_API.md` - Comprehensive API documentation

## Testing

You can test the endpoints using curl or Postman:

```bash
# Test Get Schedules
curl -X GET http://localhost:8080/api/v1/doctor/schedule \
  -H "Authorization: Bearer <token>"

# Test Add Schedule
curl -X POST http://localhost:8080/api/v1/doctor/schedule \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{...}'

# Test Request Leave
curl -X POST http://localhost:8080/api/v1/doctor/leaves \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{...}'

# Test Get Leaves
curl -X GET http://localhost:8080/api/v1/doctor/leaves \
  -H "Authorization: Bearer <token>"

# Test Approve Leave
curl -X PATCH http://localhost:8080/api/v1/doctor/leaves/{id}/approve \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{"status": "approved"}'
```

## Notes

- All endpoints require Bearer token authentication
- The implementation uses Spring Security with JWT tokens
- Database operations are transactional
- Logging is implemented for debugging
- Exception handling ensures proper error responses

