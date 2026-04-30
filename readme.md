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
| `slug`     | string  | Lọc slug của chuyên khoa       | -           |
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
- Có thể filter theo slug (`slug`).
- Hỗ trợ sorting và pagination.

## 3. Danh Sách Dịch Vụ

```
GET /api/v1/public/service
```

| Param         | Kiểu    | Mô tả                         | Mặc định |
|---------------|---------|-------------------------------|----------|
| `isFeatured`  | boolean | Lọc dịch vụ nổi bật           | -        |
| `isActive`    | boolean | Lọc dịch vụ đang hoạt động    | -        |
| `name`        | string  | Tìm kiếm theo tên dịch vụ     | -        |
| `slug`        | string  | Tìm kiếm theo slug            | -        |
| `specialtyId` | string  | Lọc theo chuyên khoa (UUID)   | -        |
| `page`        | number  | Trang hiện tại (bắt đầu từ 0) | `0`      |
| `size`        | number  | Số lượng mỗi trang            | `20`     |
| `sortBy`      | string  | Field sắp xếp                 | `id`     |
| `sortDir`     | string  | Thứ tự (`asc` / `desc`)       | `asc`    |

**Ví dụ:**

```
GET /api/v1/public/service?isActive=true&isFeatured=true&name=kham&specialtyId=123e4567-e89b-12d3-a456-426614174000&page=0&size=10&sortBy=displayOrder&sortDir=asc
```

**Mô tả:**

- API trả về danh sách dịch vụ có phân trang.
- Mặc định chỉ lấy dữ liệu chưa bị xóa (`notDeleted()`).
- Hỗ trợ:
    - Lọc dịch vụ nổi bật (`isFeatured`)
    - Lọc trạng thái hoạt động (`isActive`)
    - Tìm kiếm theo tên (`name`) hoặc slug (`slug`)
    - Lọc theo chuyên khoa (`specialtyId`)
- Dữ liệu có join sẵn `specialty`.
- Sử dụng `distinct` để tránh trùng dữ liệu khi join.
- Hỗ trợ sorting và pagination.

## 4. Danh Sách Lịch Ngoại Lệ Bác Sĩ

```
GET /api/v1/public/doctor-schedule-exception
```

| Param      | Kiểu   | Mô tả                                         | Mặc định |
|------------|--------|-----------------------------------------------|----------|
| `type`     | string | Loại ngoại lệ (`OFF`, `BUSY`, `HOLIDAY`, ...) | -        |
| `doctorId` | string | ID bác sĩ (UUID)                              | -        |
| `page`     | number | Trang hiện tại (bắt đầu từ 0)                 | `0`      |
| `size`     | number | Số lượng mỗi trang                            | `20`     |
| `sortBy`   | string | Field sắp xếp                                 | `id`     |
| `sortDir`  | string | Thứ tự (`asc` / `desc`)                       | `asc`    |

**Ví dụ:**

```
GET /api/v1/public/doctor-schedule-exceptions?doctorId=123e4567-e89b-12d3-a456-426614174000&type=OFF&page=0&size=10
```

**Mô tả:**

- API trả về danh sách lịch ngoại lệ của bác sĩ (nghỉ, bận, nghỉ lễ...).
- Mặc định chỉ lấy dữ liệu chưa bị xóa (`notDeleted()`).
- Hỗ trợ:
    - Lọc theo loại ngoại lệ (`type`)
    - Lọc theo bác sĩ (`doctorId`)
- Dữ liệu có join sẵn:
    - `doctorProfile`
    - `user` (thông tin bác sĩ)
    - `specialty` (chuyên khoa)
- Sử dụng `distinct` để tránh trùng dữ liệu khi join.
- Hỗ trợ sorting và pagination.