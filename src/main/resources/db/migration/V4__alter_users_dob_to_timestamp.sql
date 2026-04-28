-- 1. Thêm cột tạm với kiểu TIMESTAMP
ALTER TABLE users
    ADD COLUMN date_of_birth_tmp TIMESTAMP;

-- 2. Convert dữ liệu từ VARCHAR -> TIMESTAMP (format: DD/MM/YYYY)
UPDATE users
SET date_of_birth_tmp = TO_TIMESTAMP(date_of_birth, 'DD/MM/YYYY')
WHERE date_of_birth IS NOT NULL;

-- 3. (Optional) Check dữ liệu không convert được
-- SELECT * FROM users WHERE date_of_birth_tmp IS NULL AND date_of_birth IS NOT NULL;

-- 4. Xóa cột cũ
ALTER TABLE users
DROP COLUMN date_of_birth;

-- 5. Rename cột mới
ALTER TABLE users
    RENAME COLUMN date_of_birth_tmp TO date_of_birth;

-- 6. Set NOT NULL lại nếu cần
ALTER TABLE users
    ALTER COLUMN date_of_birth SET NOT NULL;