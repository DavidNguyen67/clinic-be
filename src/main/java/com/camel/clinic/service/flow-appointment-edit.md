PUT /appointments/{id}
│
▼
┌─────────────────────────────────────────────────────┐
│ 1. Fetch appointment by ID │
│ → không tìm thấy → throw "not found"             │
└─────────────────────┬───────────────────────────────┘
│
▼
┌─────────────────────────────────────────────────────┐
│ 2. Resolve target status │
│ requestBody.status != null → dùng status mới │
│ requestBody.status == null → giữ status hiện tại │
└─────────────────────┬───────────────────────────────┘
│
▼
┌─────────────────────────────────────────────────────┐
│ 3. Validate transition + role │
│ │
│ currentStatus ──→ targetStatus │
│ │ │ │
│ State machine Role check │
│    (đúng thứ tự?)       (có quyền không?)           │
│ │ │ │
│ └────────┬─────────┘ │
│ │ │
│ Fail? → throw │
└─────────────────────┬───────────────────────────────┘
│
▼
┌─────────────────────────────────────────────────────┐
│ 4. Được phép sửa thông tin chi tiết không? │
│ │
│ canEditDetails = currentStatus == PENDING │
│ OR isReactivation │
│                  (CANCELLED → PENDING)              │
│ │
│ canEditDetails == false │
│ nhưng request có thay đổi detail → throw │
└─────────────────────┬───────────────────────────────┘
│
┌────────┴────────┐
│ │
canEditDetails chỉ đổi
== true status
│ │
▼ │
Resolve doctor + date │
(dùng giá trị mới nếu có, │
fallback về giá trị cũ)     │
│ │
▼ │
┌─────────────────────────────────────────────────────┐
│ 5. Check doctor availability │
│ Trigger khi:                                     │
│ - doctorChanged (đổi bác sĩ)                     │
│ - dateChanged   (đổi ngày)                       │
│ - isReactivation (slot cũ có thể đã bị chiếm)   │
│ │
│ isDoctorAvailable()? Không → throw │
│ isExistAppointmentAt()? Có → throw │
└─────────────────────┬───────────────────────────────┘
│
▼
┌─────────────────────────────────────────────────────┐
│ 6. Apply changes │
│ │
│ Luôn set:                                        │
│ - status         (targetStatus)                  │
│ - appointmentDate │
│ │
│ Chỉ set khi canEditDetails:                      │
│ - doctorProfile + specialty (nếu doctorChanged)  │
│ - bookingType, reason, symptoms, notes │
└─────────────────────┬───────────────────────────────┘
│
▼
save() → ResponseDto