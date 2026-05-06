# Appointment Update — Payload & Flow

## PATIENT

### Tự hủy lịch

```json
// currentStatus: PENDING
{
  "status": "CANCELLED"
}
```

### Reactivate lịch đã hủy — không đổi gì

```json
// currentStatus: CANCELLED
{
  "status": "PENDING"
}
```

### Reactivate + đổi bác sĩ + đổi ngày

```json
// currentStatus: CANCELLED
{
  "status": "PENDING",
  "doctorProfileId": "doctor-uuid-moi",
  "appointmentDate": "2026-05-10T09:00:00",
  "reason": "Khám lại",
  "symptoms": "Đau đầu",
  "notes": "Buổi sáng",
  "bookingType": "ONLINE"
}
```

### Update thông tin khi đang PENDING

```json
// currentStatus: PENDING
{
  "doctorProfileId": "doctor-uuid-moi",
  "appointmentDate": "2026-05-12T14:00:00",
  "reason": "Khám tổng quát",
  "symptoms": "Mệt mỏi",
  "notes": "Ưu tiên buổi chiều",
  "bookingType": "OFFLINE"
}
```

---

## RECEPTIONIST

### Confirm lịch

```json
// currentStatus: PENDING
{
  "status": "CONFIRMED"
}
```

### Check-in bệnh nhân

```json
// currentStatus: CONFIRMED
{
  "status": "CHECKED_IN"
}
```

---

## DOCTOR

### Bắt đầu khám

```json
// currentStatus: CHECKED_IN
{
  "status": "IN_PROGRESS"
}
```

### Hoàn thành khám

```json
// currentStatus: IN_PROGRESS
{
  "status": "COMPLETED"
}
```

### Đánh dấu no-show

```json
// currentStatus: CONFIRMED hoặc CHECKED_IN
{
  "status": "NO_SHOW"
}
```

---

## ADMIN

### Ép chuyển bất kỳ status hợp lệ

```json
// currentStatus: bất kỳ (trừ terminal)
{
  "status": "CANCELLED"
}
```

---

## Case lỗi — để test validate

### ❌ Nhảy cóc status (bất kỳ role)

```json
// currentStatus: PENDING
// → state machine không cho PENDING → COMPLETED
{
  "status": "COMPLETED"
}
// throw: "Invalid status transition: PENDING → COMPLETED"
```

### ❌ DOCTOR cố confirm lịch

```json
// currentStatus: PENDING
{
  "status": "CONFIRMED"
}
// throw: "Role DOCTOR is not allowed to set status to CONFIRMED"
```

### ADMIN reactivate (chỉ PATIENT, ADMIN được)

```json
// currentStatus: CANCELLED
{
  "status": "PENDING"
}
```

### ❌ Sửa detail khi không phải PENDING

```json
// currentStatus: CONFIRMED
{
  "status": "CHECKED_IN",
  "reason": "Muốn đổi lý do khám"
}
// throw: "Cannot change appointment details when status is CONFIRMED"
```

### ❌ PATIENT reactivate nhưng slot đã bị chiếm

```json
// currentStatus: CANCELLED
{
  "status": "PENDING"
  // không đổi doctor/date → dùng lại giá trị cũ
  // → check availability với doctor + date cũ
}
// throw: "Doctor already has an appointment at the selected time..."
```

---

## Tóm tắt ai được gửi payload nào

| Role         |  Có status  | Có detail | Hợp lệ khi                                                |
|--------------|:-----------:|:---------:|-----------------------------------------------------------|
| PATIENT      |  CANCELLED  |     ✗     | currentStatus là PENDING/CONFIRMED/CHECKED_IN/IN_PROGRESS |
| PATIENT      |   PENDING   |    ✗/✓    | currentStatus là CANCELLED                                |
| PATIENT      |      ✗      |     ✓     | currentStatus là PENDING                                  |
| RECEPTIONIST |  CONFIRMED  |     ✗     | currentStatus là PENDING                                  |
| RECEPTIONIST | CHECKED_IN  |     ✗     | currentStatus là CONFIRMED                                |
| DOCTOR       | IN_PROGRESS |     ✗     | currentStatus là CHECKED_IN                               |
| DOCTOR       |  COMPLETED  |     ✗     | currentStatus là IN_PROGRESS                              |
| DOCTOR       |   NO_SHOW   |     ✗     | currentStatus là CONFIRMED/CHECKED_IN                     |
| ADMIN        |   bất kỳ    |     ✗     | transition hợp lệ theo state machine                      |

---

## State machine

```
PENDING      → CONFIRMED, CANCELLED
CONFIRMED    → CHECKED_IN, CANCELLED, NO_SHOW
CHECKED_IN   → IN_PROGRESS, CANCELLED, NO_SHOW
IN_PROGRESS  → COMPLETED, CANCELLED
COMPLETED    → (terminal)
CANCELLED    → PENDING (chỉ PATIENT)
NO_SHOW      → (terminal)
```

## Role permission

| Transition               | PATIENT | DOCTOR | RECEPTIONIST | ADMIN |
|--------------------------|:-------:|:------:|:------------:|:-----:|
| PENDING → CONFIRMED      |    ✗    |   ✗    |      ✓       |   ✓   |
| PENDING → CANCELLED      |    ✓    |   ✗    |      ✓       |   ✓   |
| CONFIRMED → CHECKED_IN   |    ✗    |   ✓    |      ✓       |   ✓   |
| CONFIRMED → NO_SHOW      |    ✗    |   ✓    |      ✗       |   ✓   |
| CONFIRMED → CANCELLED    |    ✓    |   ✗    |      ✓       |   ✓   |
| CHECKED_IN → IN_PROGRESS |    ✗    |   ✓    |      ✗       |   ✓   |
| CHECKED_IN → NO_SHOW     |    ✗    |   ✓    |      ✗       |   ✓   |
| CHECKED_IN → CANCELLED   |    ✓    |   ✗    |      ✓       |   ✓   |
| IN_PROGRESS → COMPLETED  |    ✗    |   ✓    |      ✗       |   ✓   |
| IN_PROGRESS → CANCELLED  |    ✓    |   ✗    |      ✓       |   ✓   |
| CANCELLED → PENDING      |    ✓    |   ✗    |      ✗       |   ✓   |