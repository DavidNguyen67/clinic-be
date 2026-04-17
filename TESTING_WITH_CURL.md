# Doctor Schedule Management API - Testing with cURL

## Prerequisites
```bash
# Store your bearer token
TOKEN="your-jwt-token-here"
BASE_URL="http://localhost:8080/api/v1"
```

---

## 1. Get Doctor Schedule

### Command
```bash
curl -X GET "${BASE_URL}/doctor/schedule" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json"
```

### Expected Response (200 OK)
```json
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

### Error Response (401 Unauthorized)
```json
{
  "error": "User not authenticated"
}
```

---

## 2. Add Doctor Schedule

### Command
```bash
curl -X POST "${BASE_URL}/doctor/schedule" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek": 1,
    "startTime": "08:00:00",
    "endTime": "17:00:00",
    "slotDuration": 30,
    "maxPatientsPerSlot": 1,
    "location": "Room 101",
    "isActive": true
  }'
```

### With Pretty Print
```bash
curl -X POST "${BASE_URL}/doctor/schedule" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek": 1,
    "startTime": "08:00:00",
    "endTime": "17:00:00",
    "slotDuration": 30,
    "maxPatientsPerSlot": 1,
    "location": "Room 101",
    "isActive": true
  }' | jq .
```

### Expected Response (201 Created)
```json
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

### Add Multiple Schedules (Different Days)
```bash
# Monday Schedule
curl -X POST "${BASE_URL}/doctor/schedule" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek": 1,
    "startTime": "08:00:00",
    "endTime": "17:00:00",
    "slotDuration": 30,
    "maxPatientsPerSlot": 1,
    "location": "Room 101"
  }'

# Tuesday Schedule
curl -X POST "${BASE_URL}/doctor/schedule" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek": 2,
    "startTime": "09:00:00",
    "endTime": "18:00:00",
    "slotDuration": 45,
    "maxPatientsPerSlot": 2,
    "location": "Room 102"
  }'

# Wednesday Schedule
curl -X POST "${BASE_URL}/doctor/schedule" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek": 3,
    "startTime": "08:00:00",
    "endTime": "12:00:00",
    "slotDuration": 30,
    "maxPatientsPerSlot": 1,
    "location": "Room 101"
  }'
```

---

## 3. Delete Doctor Schedule

### Command (Replace {id} with actual schedule UUID)
```bash
SCHEDULE_ID="550e8400-e29b-41d4-a716-446655440000"

curl -X DELETE "${BASE_URL}/doctor/schedule/${SCHEDULE_ID}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json"
```

### Expected Response (204 No Content)
```
(Empty response body)
```

### Error Response (404 Not Found)
```json
{
  "error": "Schedule not found"
}
```

---

## 4. Request Doctor Leave

### Command (Full day leave)
```bash
curl -X POST "${BASE_URL}/doctor/leaves" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "leaveDate": "25/12/2024",
    "reason": "Christmas holiday"
  }'
```

### Command (Partial day leave)
```bash
curl -X POST "${BASE_URL}/doctor/leaves" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "leaveDate": "25/12/2024",
    "startTime": "08:00:00",
    "endTime": "12:00:00",
    "reason": "Personal appointment in the morning"
  }'
```

### Expected Response (201 Created)
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "leaveDate": "25/12/2024",
  "startTime": "08:00:00",
  "endTime": "12:00:00",
  "reason": "Personal appointment in the morning",
  "status": "pending",
  "doctorName": "Dr. John Doe",
  "doctorId": "550e8400-e29b-41d4-a716-446655440002"
}
```

### Multiple Leave Requests
```bash
# Leave 1
curl -X POST "${BASE_URL}/doctor/leaves" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "leaveDate": "24/12/2024",
    "reason": "Christmas preparation"
  }'

# Leave 2
curl -X POST "${BASE_URL}/doctor/leaves" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "leaveDate": "25/12/2024",
    "reason": "Christmas day"
  }'

# Leave 3
curl -X POST "${BASE_URL}/doctor/leaves" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "leaveDate": "26/12/2024",
    "reason": "Boxing day"
  }'
```

---

## 5. Get Doctor Leaves

### Doctor - Get Own Leaves
```bash
curl -X GET "${BASE_URL}/doctor/leaves" \
  -H "Authorization: Bearer ${DOCTOR_TOKEN}" \
  -H "Content-Type: application/json"
```

### Admin - Get All Pending Leaves
```bash
curl -X GET "${BASE_URL}/doctor/leaves" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json"
```

### Admin - Get Leaves for Specific Doctor
```bash
DOCTOR_ID="550e8400-e29b-41d4-a716-446655440002"

curl -X GET "${BASE_URL}/doctor/leaves?doctorId=${DOCTOR_ID}" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json"
```

### Expected Response (200 OK)
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "leaveDate": "25/12/2024",
    "startTime": "08:00:00",
    "endTime": "17:00:00",
    "reason": "Christmas holiday",
    "status": "pending",
    "doctorName": "Dr. John Doe",
    "doctorId": "550e8400-e29b-41d4-a716-446655440002"
  }
]
```

---

## 6. Approve Doctor Leave

### Approve Leave
```bash
LEAVE_ID="550e8400-e29b-41d4-a716-446655440001"

curl -X PATCH "${BASE_URL}/doctor/leaves/${LEAVE_ID}/approve" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "approved"
  }'
```

### Reject Leave with Reason
```bash
LEAVE_ID="550e8400-e29b-41d4-a716-446655440001"

curl -X PATCH "${BASE_URL}/doctor/leaves/${LEAVE_ID}/approve" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "rejected",
    "rejectionReason": "Insufficient notice period"
  }'
```

### Expected Response (200 OK)
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "leaveDate": "25/12/2024",
  "startTime": "08:00:00",
  "endTime": "17:00:00",
  "reason": "Christmas holiday",
  "status": "approved",
  "doctorName": "Dr. John Doe",
  "doctorId": "550e8400-e29b-41d4-a716-446655440002"
}
```

### Error Response (Not Admin)
```json
{
  "error": "Only admins can approve leaves"
}
```

---

## Advanced Testing Scripts

### Bash Script - Test All Endpoints
```bash
#!/bin/bash

TOKEN="your-jwt-token-here"
ADMIN_TOKEN="admin-jwt-token-here"
BASE_URL="http://localhost:8080/api/v1"

echo "========== Testing Doctor Schedule Management API =========="

# 1. Get schedules
echo -e "\n1. Getting doctor schedules..."
curl -s -X GET "${BASE_URL}/doctor/schedule" \
  -H "Authorization: Bearer ${TOKEN}" | jq .

# 2. Add schedule
echo -e "\n2. Adding new schedule..."
SCHEDULE_RESPONSE=$(curl -s -X POST "${BASE_URL}/doctor/schedule" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek": 1,
    "startTime": "08:00:00",
    "endTime": "17:00:00",
    "slotDuration": 30,
    "maxPatientsPerSlot": 1,
    "location": "Room 101"
  }')

echo "$SCHEDULE_RESPONSE" | jq .
SCHEDULE_ID=$(echo "$SCHEDULE_RESPONSE" | jq -r '.id')

# 3. Request leave
echo -e "\n3. Requesting leave..."
LEAVE_RESPONSE=$(curl -s -X POST "${BASE_URL}/doctor/leaves" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "leaveDate": "25/12/2024",
    "reason": "Personal reasons"
  }')

echo "$LEAVE_RESPONSE" | jq .
LEAVE_ID=$(echo "$LEAVE_RESPONSE" | jq -r '.id')

# 4. Get leaves
echo -e "\n4. Getting leaves..."
curl -s -X GET "${BASE_URL}/doctor/leaves" \
  -H "Authorization: Bearer ${TOKEN}" | jq .

# 5. Approve leave (as admin)
echo -e "\n5. Approving leave..."
curl -s -X PATCH "${BASE_URL}/doctor/leaves/${LEAVE_ID}/approve" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "approved"
  }' | jq .

# 6. Delete schedule
echo -e "\n6. Deleting schedule..."
curl -s -X DELETE "${BASE_URL}/doctor/schedule/${SCHEDULE_ID}" \
  -H "Authorization: Bearer ${TOKEN}"

echo -e "\n========== Testing Complete =========="
```

### Save and Run
```bash
chmod +x test_api.sh
./test_api.sh
```

---

## Common Test Scenarios

### Scenario 1: Setup Doctor Schedule for Week
```bash
for day in {1..5}; do
  curl -X POST "${BASE_URL}/doctor/schedule" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "Content-Type: application/json" \
    -d "{
      \"dayOfWeek\": ${day},
      \"startTime\": \"08:00:00\",
      \"endTime\": \"17:00:00\",
      \"slotDuration\": 30,
      \"maxPatientsPerSlot\": 1,
      \"location\": \"Room 101\"
    }"
  echo "Added schedule for day ${day}"
done
```

### Scenario 2: Request and Approve Multiple Leaves
```bash
# Request leaves
for day in 24 25 26; do
  LEAVE_ID=$(curl -s -X POST "${BASE_URL}/doctor/leaves" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "Content-Type: application/json" \
    -d "{
      \"leaveDate\": \"${day}/12/2024\",
      \"reason\": \"Holiday\"
    }" | jq -r '.id')
  
  # Approve leave (as admin)
  curl -X PATCH "${BASE_URL}/doctor/leaves/${LEAVE_ID}/approve" \
    -H "Authorization: Bearer ${ADMIN_TOKEN}" \
    -H "Content-Type: application/json" \
    -d '{"status": "approved"}'
  
  echo "Processed leave for day ${day}"
done
```

---

## Error Handling Test Cases

### Test Invalid Day of Week
```bash
curl -X POST "${BASE_URL}/doctor/schedule" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek": 10,
    "startTime": "08:00:00",
    "endTime": "17:00:00"
  }'
# Expected: 400 Bad Request - "Day of week must be between 0-6"
```

### Test Missing Required Fields
```bash
curl -X POST "${BASE_URL}/doctor/leaves" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "leaveDate": "25/12/2024"
  }'
# Expected: 400 Bad Request - "Reason is required"
```

### Test Unauthorized Access
```bash
curl -X GET "${BASE_URL}/doctor/schedule"
# Expected: 401 Unauthorized
```

---

## Performance Testing

### Load Test with Apache Bench
```bash
# Install: sudo apt-get install apache2-utils

# Test GET endpoint
ab -n 1000 -c 10 -H "Authorization: Bearer ${TOKEN}" \
  http://localhost:8080/api/v1/doctor/schedule

# Test POST endpoint
ab -n 100 -c 5 -p payload.json -T application/json \
  -H "Authorization: Bearer ${TOKEN}" \
  http://localhost:8080/api/v1/doctor/leaves
```

---

## Debugging

### Enable Verbose Output
```bash
curl -v -X GET "${BASE_URL}/doctor/schedule" \
  -H "Authorization: Bearer ${TOKEN}"
```

### Save Response to File
```bash
curl -X GET "${BASE_URL}/doctor/schedule" \
  -H "Authorization: Bearer ${TOKEN}" \
  > response.json

cat response.json | jq .
```

### Check Response Headers
```bash
curl -i -X GET "${BASE_URL}/doctor/schedule" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

