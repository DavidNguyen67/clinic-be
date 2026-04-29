# Hướng Dẫn Sử Dụng API

---

## 1. Danh Sách Bác sĩ

```
GET /api/v1/public/doctor-profile
```

| Param           | Kiểu    | Mô tả                         | Mặc định    |
|-----------------|---------|-------------------------------|-------------|
| `isFeatured`    | boolean | Lọc bác sĩ nổi bật            | -           |
| `fullName`      | string  | Tìm kiếm theo tên bác sĩ      | -           |
| `specialtyName` | string  | Tìm kiếm theo tên chuyên khoa | -           |
| `page`          | number  | Trang hiện tại (bắt đầu từ 0) | `0`         |
| `size`          | number  | Số lượng mỗi trang            | `20`        |
| `sortBy`        | string  | Field sắp xếp                 | `createdAt` |
| `sortDir`       | string  | Thứ tự (`asc` / `desc`)       | `asc`       |

**Ví dụ:**

```
GET /api/v1/public/doctor-profile?isFeatured=true&fullName=nguyen&specialtyName=tim&page=0&size=10&sortBy=rating&sortDir=desc
```

**Mô tả:**

- API trả về danh sách bác sĩ có phân trang.
- Mặc định chỉ lấy dữ liệu chưa bị xóa (`notDeleted()`).
- Hỗ trợ:
    - Filter bác sĩ nổi bật (`isFeatured`)
    - Tìm kiếm theo tên bác sĩ (`fullName`)
    - Tìm kiếm theo chuyên khoa (`specialtyName`)
- Hỗ trợ sorting và pagination.

## 2. Danh Sách Chuyên Khoa

```
GET /api/v1/public/specialty
```

| Param      | Kiểu    | Mô tả                          | Mặc định    |
|------------|---------|--------------------------------|-------------|
| `isActive` | boolean | Lọc chuyên khoa đang hoạt động | `true`      |
| `page`     | number  | Trang hiện tại (bắt đầu từ 0)  | `0`         |
| `size`     | number  | Số lượng mỗi trang             | `20`        |
| `sortBy`   | string  | Field sắp xếp                  | `createdAt` |
| `sortDir`  | string  | Thứ tự (`asc` / `desc`)        | `asc`       |

**Ví dụ:**

```
GET /api/v1/public/specialties?isActive=true&page=0&size=10&sortBy=displayOrder&sortDir=asc
```

**Mô tả:**

- API trả về danh sách chuyên khoa có phân trang.
- Mặc định chỉ lấy các chuyên khoa chưa bị xóa (`notDeleted()`).
- Có thể filter theo trạng thái hoạt động (`isActive`).
- Hỗ trợ sorting và pagination.