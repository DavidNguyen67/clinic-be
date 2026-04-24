-- V2__add_specialty_to_appointments.sql

ALTER TABLE appointments
    ADD COLUMN specialty_id UUID,
    ADD CONSTRAINT fk_appointment_specialty
        FOREIGN KEY (specialty_id)
        REFERENCES specialties(id);

-- Backfill: lấy specialty từ service của appointment hiện có
UPDATE appointments a
SET specialty_id = cs.specialty_id
    FROM services cs
WHERE a.service_id = cs.id
  AND cs.specialty_id IS NOT NULL;