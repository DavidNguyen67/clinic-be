-- V7__rename_tables.sql

-- Rename doctors -> doctor_profile
ALTER TABLE doctors RENAME TO doctor_profile;

-- Rename staff -> staff_profile
ALTER TABLE staff RENAME TO staff_profile;

-- Ví dụ rename constraint (nếu cần)
ALTER TABLE doctor_profile RENAME CONSTRAINT fk_doctor_user TO fk_doctor_profile_user;
ALTER TABLE doctor_profile RENAME CONSTRAINT fk_doctor_specialty TO fk_doctor_profile_specialty;
ALTER TABLE staff_profile RENAME CONSTRAINT fk_staff_user TO fk_staff_profile_user;