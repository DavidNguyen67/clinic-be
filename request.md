# BaseService API Request Examples

## 1. CREATE — POST /api/{resource}

**Request:**

```http
POST /api/patients
Content-Type: application/json

{
  "name": "Nguyen Van A",
  "phone": "0901234567",
  "email": "nguyenvana@gmail.com",
  "dateOfBirth": "1990-05-15"
}
```

**Response 201:**

```json
{
  "id": "uuid-1234",
  "name": "Nguyen Van A",
  "phone": "0901234567",
  "email": "nguyenvana@gmail.com",
  "dateOfBirth": "1990-05-15",
  "createdAt": "2026-04-12T08:00:00",
  "updatedAt": "2026-04-12T08:00:00"
}
```

---

## 2. LIST — GET /api/{resource}

**Request:**

```http
GET /api/patients?page=0&size=10&sortBy=name&sortDir=asc
```

| Query Param | Type   | Default | Mô tả                     |
|-------------|--------|---------|---------------------------|
| page        | int    | 0       | Số trang (bắt đầu từ 0)   |
| size        | int    | 20      | Số bản ghi mỗi trang      |
| sortBy      | string | "id"    | Field để sort             |
| sortDir     | string | "asc"   | Chiều sort: asc hoặc desc |

**Response 200:**

```json
{
  "data": [
    {
      "id": "uuid-1234",
      "name": "Nguyen Van A",
      "phone": "0901234567"
    },
    {
      "id": "uuid-5678",
      "name": "Tran Thi B",
      "phone": "0907654321"
    }
  ],
  "page": 0,
  "size": 10,
  "totalItems": 2,
  "totalPages": 1
}
```

---

## 3. RETRIEVE — GET /api/{resource}/{id}

**Request (lấy toàn bộ fields):**

```http
GET /api/patients/uuid-1234
```

**Request (chỉ lấy một số fields):**

```http
GET /api/patients/uuid-1234?fields=id,name,phone
```

**Response 200:**

```json
{
  "id": "uuid-1234",
  "name": "Nguyen Van A",
  "phone": "0901234567"
}
```

**Response 404:**

```json
{
  "error": "Entity not found with id: uuid-1234"
}
```

---

## 4. UPDATE — PUT /api/{resource}/{id}

**Request:**

```http
PUT /api/patients/uuid-1234?fields=id,name,phone
Content-Type: application/json

{
  "name": "Nguyen Van A (updated)",
  "phone": "0999999999"
}
```

| Query Param | Type   | Mô tả                                               |
|-------------|--------|-----------------------------------------------------|
| fields      | string | Comma-separated fields muốn nhận lại trong response |

**Response 200:**

```json
{
  "id": "uuid-1234",
  "name": "Nguyen Van A (updated)",
  "phone": "0999999999"
}
```

**Response 404:**

```json
{
  "error": "Entity not found with id: uuid-1234"
}
```

---

## 5. DELETE — DELETE /api/{resource}/{id}

**Request:**

```http
DELETE /api/patients/uuid-1234
```

**Response 204:** *(No Content)*

**Response 404:**

```json
{
  "error": "Entity not found with id: uuid-1234"
}
```

---

## 6. GET BY IDS — POST /api/{resource}/batch

**Request:**

```http
POST /api/patients/batch?fields=id,name,email
Content-Type: application/json

{
  "ids": [
    "uuid-1234",
    "uuid-5678",
    "uuid-9999"
  ]
}
```

| Query Param | Type   | Mô tả                                               |
|-------------|--------|-----------------------------------------------------|
| fields      | string | Comma-separated fields muốn nhận lại trong response |

**Response 200:**

```json
[
  {
    "id": "uuid-1234",
    "name": "Nguyen Van A",
    "email": "nguyenvana@gmail.com"
  },
  {
    "id": "uuid-5678",
    "name": "Tran Thi B",
    "email": "tranthib@gmail.com"
  }
]
```

> **Lưu ý:** id không tồn tại sẽ bị bỏ qua, không báo lỗi.

---

## Error Response chung

| HTTP Status | Ý nghĩa                      |
|-------------|------------------------------|
| 201         | Tạo mới thành công           |
| 200         | Thành công                   |
| 204         | Xóa thành công (No Content)  |
| 400         | Dữ liệu đầu vào không hợp lệ |
| 404         | Không tìm thấy bản ghi       |
| 500         | Lỗi server                   |

**Response lỗi mẫu:**

```json
{
  "error": "Mô tả lỗi ngắn gọn",
  "message": "Chi tiết lỗi từ exception"
}
```