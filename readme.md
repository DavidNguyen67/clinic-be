# Hướng Dẫn Sử Dụng API

---

## 1. Bác Sĩ Nổi Bật

```
GET /api/v1/public/doctor-profile
```

| Param        | Kiểu   | Mô tả                             | Mặc định    |
|--------------|--------|-----------------------------------|-------------|
| `isFeatured` | string | Lọc bác sĩ nổi bật (`yes` / `no`) | -           |
| `page`       | number | Trang hiện tại (bắt đầu từ 0)     | `0`         |
| `size`       | number | Số lượng mỗi trang                | `20`        |
| `sortBy`     | string | Field sắp xếp                     | `createdAt` |
| `sortDir`    | string | Thứ tự (`asc` / `desc`)           | `asc`       |