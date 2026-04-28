-- V5__remove_status_from_doctor_and_staff.sql
ALTER TABLE staff DROP COLUMN status;
ALTER TABLE doctors DROP COLUMN status;
