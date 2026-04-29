# Hướng Dẫn Sử Dụng API

---

## 1. Bác Sĩ Nổi Bật

```
GET /doctors/featured
```

| Param   | Kiểu   | Mô tả             | Ví dụ     |
|---------|--------|-------------------|-----------|
| `limit` | number | Số kết quả trả về | `limit=5` |
| `page`  | number | Trang hiện tại    | `page=1`  |

**Ví dụ:**

```
GET /doctors/featured?limit=5&page=1
```

---

## 2. Danh Sách Bác Sĩ (Filter)

```
GET /doctors
```

| Param         | Kiểu    | Mô tả                                  | Ví dụ                |
|---------------|---------|----------------------------------------|----------------------|
| `specialty`   | string  | Chuyên khoa                            | `specialty=tim-mach` |
| `city`        | string  | Thành phố / khu vực                    | `city=hanoi`         |
| `gender`      | string  | Giới tính (`male` / `female`)          | `gender=female`      |
| `available`   | boolean | Chỉ lấy bác sĩ đang nhận lịch          | `available=true`     |
| `min_rating`  | number  | Đánh giá tối thiểu (0–5)               | `min_rating=4.5`     |
| `hospital_id` | string  | Lọc theo bệnh viện/phòng khám          | `hospital_id=hv_012` |
| `sort_by`     | string  | Sắp xếp: `rating` / `reviews` / `name` | `sort_by=rating`     |
| `order`       | string  | Thứ tự: `asc` / `desc`                 | `order=desc`         |
| `page`        | number  | Trang hiện tại                         | `page=1`             |
| `limit`       | number  | Số lượng mỗi trang                     | `limit=20`           |

**Ví dụ:**

```
GET /doctors?specialty=tim-mach&city=hanoi&available=true&sort_by=rating&order=desc
```

---

## 3. Chi Tiết Bác Sĩ

```
GET /doctors/{id}
```

**Ví dụ:**

```
GET /doctors/dr_001
```