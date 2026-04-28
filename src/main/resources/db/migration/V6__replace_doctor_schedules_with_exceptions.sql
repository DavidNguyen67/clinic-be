-- V6__replace_doctor_schedules_with_exceptions.sql

DROP TABLE IF EXISTS doctor_schedules;

CREATE TABLE doctor_schedule_exceptions (
    id            BINARY(16)   NOT NULL PRIMARY KEY,
    doctor_id     BINARY(16)   NOT NULL,
    exception_date DATE        NOT NULL,
    type          VARCHAR(20)  NOT NULL,
    reason        TEXT,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP    NULL,

    CONSTRAINT fk_exception_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_exception_date (exception_date),
    INDEX idx_doctor_date (doctor_id, exception_date)
);