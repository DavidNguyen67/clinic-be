-- =============================================================================
-- V1__init_clinic_schema_merged.sql
-- Schema tổng hợp từ V1 -> V7 (trạng thái cuối cùng)
-- Các thay đổi đã được áp dụng trực tiếp:
--   V2: Thêm specialty_id vào appointment
--   V3: Bỏ date_of_birth khỏi patient_profile
--   V4: Chuyển date_of_birth trong user từ VARCHAR -> TIMESTAMP
--   V5: Bỏ cột status khỏi doctors và staffProfile
--   V6: Thay doctor_schedules bằng doctor_schedule_exception
--   V7: Đổi tên doctors -> doctor_profile, staffProfile -> staff_profile
-- =============================================================================

CREATE TABLE role
(
    id   UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP,
    deleted_at     TIMESTAMP,
    email          VARCHAR(255) NOT NULL UNIQUE,
    date_of_birth  TIMESTAMP    NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    phone          VARCHAR(20)  NOT NULL UNIQUE,
    full_name      VARCHAR(255) NOT NULL,
    role           VARCHAR(20)  NOT NULL,
    path_avatar    VARCHAR(500),
    status         VARCHAR(20)  NOT NULL,
    gender         VARCHAR(20)  NOT NULL,
    email_verified BOOLEAN      NOT NULL DEFAULT FALSE,
    phone_verified BOOLEAN      NOT NULL DEFAULT FALSE,
    last_login     TIMESTAMP
);

CREATE TABLE specialty
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP,
    deleted_at     TIMESTAMP,
    name           VARCHAR(255) NOT NULL,
    slug           VARCHAR(255) NOT NULL UNIQUE,
    description    TEXT,
    image          VARCHAR(500),
    display_order  INTEGER      NOT NULL DEFAULT 0,
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
    specialty_type VARCHAR(50)  NOT NULL DEFAULT 'GENERAL'
);

CREATE TABLE services
(
    id                UUID PRIMARY KEY,
    created_at        TIMESTAMP      NOT NULL,
    updated_at        TIMESTAMP,
    deleted_at        TIMESTAMP,
    specialty_id      UUID,
    name              VARCHAR(255)   NOT NULL,
    slug              VARCHAR(255)   NOT NULL UNIQUE,
    description       TEXT,
    price             NUMERIC(10, 2) NOT NULL,
    promotional_price NUMERIC(10, 2),
    duration          INTEGER        NOT NULL DEFAULT 30,
    image             VARCHAR(500),
    is_featured       BOOLEAN        NOT NULL DEFAULT FALSE,
    is_active         BOOLEAN        NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_service_specialty FOREIGN KEY (specialty_id) REFERENCES specialty (id)
);

CREATE TABLE patient_profile
(
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP   NOT NULL,
    updated_at       TIMESTAMP,
    deleted_at       TIMESTAMP,
    user_id          UUID        NOT NULL UNIQUE,
    patient_code     VARCHAR(20) NOT NULL UNIQUE,
    gender           VARCHAR(10),
    address          TEXT,
    insurance_number VARCHAR(100),
    blood_type       VARCHAR(5),
    allergies        TEXT,
    chronic_diseases TEXT,
    loyalty_points   INTEGER     NOT NULL DEFAULT 0,
    total_visits     INTEGER     NOT NULL DEFAULT 0,
    CONSTRAINT fk_patient_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE doctor_profile
(
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP      NOT NULL,
    updated_at       TIMESTAMP,
    deleted_at       TIMESTAMP,
    user_id          UUID           NOT NULL UNIQUE,
    doctor_code      VARCHAR(20)    NOT NULL UNIQUE,
    specialty_id     UUID           NOT NULL,
    degree           VARCHAR(100),
    experience_years INTEGER        NOT NULL DEFAULT 0,
    education        TEXT,
    bio              TEXT,
    consultation_fee NUMERIC(10, 2) NOT NULL DEFAULT 0,
    average_rating   NUMERIC(3, 2)  NOT NULL DEFAULT 0,
    total_reviews    INTEGER        NOT NULL DEFAULT 0,
    total_patients   INTEGER        NOT NULL DEFAULT 0,
    is_featured      BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_doctor_profile_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_doctor_profile_specialty FOREIGN KEY (specialty_id) REFERENCES specialty (id)
);

CREATE TABLE staff_profile
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP   NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    user_id    UUID        NOT NULL UNIQUE,
    staff_code VARCHAR(20) NOT NULL UNIQUE,
    position   VARCHAR(100),
    department VARCHAR(100),
    hire_date  TIMESTAMP,
    CONSTRAINT fk_staff_profile_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE appointment
(
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP   NOT NULL,
    updated_at       TIMESTAMP,
    deleted_at       TIMESTAMP,
    appointment_code VARCHAR(20) NOT NULL UNIQUE,
    patient_id       UUID        NOT NULL,
    doctor_id        UUID        NOT NULL,
    service_id       UUID,
    specialty_id     UUID,
    appointment_date TIMESTAMP   NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    booking_type     VARCHAR(20) NOT NULL DEFAULT 'ONLINE',
    reason           TEXT,
    symptoms         TEXT,
    notes            TEXT,
    queue_number     INTEGER,
    CONSTRAINT fk_appointment_patient  FOREIGN KEY (patient_id)   REFERENCES patient_profile (id),
    CONSTRAINT fk_appointment_doctor   FOREIGN KEY (doctor_id)    REFERENCES doctor_profile (id),
    CONSTRAINT fk_appointment_service  FOREIGN KEY (service_id)   REFERENCES services (id),
    CONSTRAINT fk_appointment_specialty FOREIGN KEY (specialty_id) REFERENCES specialty (id)
);

CREATE TABLE doctor_schedule_exception
(
    id             UUID         NOT NULL PRIMARY KEY,
    doctor_id      UUID         NOT NULL,
    exception_date TIMESTAMP NOT NULL,
    type           VARCHAR(20)  NOT NULL,
    reason         TEXT,
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted_at     TIMESTAMP    NULL,
    CONSTRAINT fk_exception_doctor FOREIGN KEY (doctor_id) REFERENCES doctor_profile (id)
);

CREATE TABLE doctor_performance
(
    id                     UUID           NOT NULL PRIMARY KEY,
    created_at             TIMESTAMP      NOT NULL,
    updated_at             TIMESTAMP,
    deleted_at             TIMESTAMP,
    doctor_id              UUID           NOT NULL,
    month                  INTEGER        NOT NULL,
    year                   INTEGER        NOT NULL,
    total_appointments     INTEGER        NOT NULL DEFAULT 0,
    completed_appointments INTEGER        NOT NULL DEFAULT 0,
    cancelled_appointments INTEGER        NOT NULL DEFAULT 0,
    total_patients         INTEGER        NOT NULL DEFAULT 0,
    average_rating         NUMERIC(3, 2),
    total_revenue          NUMERIC(12, 2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_performance_doctor FOREIGN KEY (doctor_id) REFERENCES doctor_profile (id),
    CONSTRAINT unique_doctor_month UNIQUE (doctor_id, month, year)
);

CREATE TABLE review
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP   NOT NULL,
    updated_at     TIMESTAMP,
    deleted_at     TIMESTAMP,
    patient_id     UUID        NOT NULL,
    doctor_id      UUID,
    appointment_id UUID,
    rating         INTEGER     NOT NULL,
    title          VARCHAR(255),
    content        TEXT,
    status         VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_review_patient     FOREIGN KEY (patient_id)     REFERENCES patient_profile (id),
    CONSTRAINT fk_review_doctor      FOREIGN KEY (doctor_id)      REFERENCES doctor_profile (id),
    CONSTRAINT fk_review_appointment FOREIGN KEY (appointment_id) REFERENCES appointment (id),
    CONSTRAINT unique_appointment_id UNIQUE (appointment_id)
);

CREATE TABLE medical_record
(
    id              UUID PRIMARY KEY,
    created_at      TIMESTAMP   NOT NULL,
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    record_code     VARCHAR(20) NOT NULL UNIQUE,
    appointment_id  UUID        NOT NULL UNIQUE,
    patient_id      UUID        NOT NULL,
    doctor_id       UUID        NOT NULL,
    chief_complaint TEXT,
    vital_signs     JSON,
    diagnosis       TEXT        NOT NULL,
    treatment_plan  TEXT,
    follow_up_date  TIMESTAMP,
    doctor_notes    TEXT,
    CONSTRAINT fk_record_appointment FOREIGN KEY (appointment_id) REFERENCES appointment (id),
    CONSTRAINT fk_record_patient     FOREIGN KEY (patient_id)     REFERENCES patient_profile (id),
    CONSTRAINT fk_record_doctor      FOREIGN KEY (doctor_id)      REFERENCES doctor_profile (id)
);

CREATE TABLE medication
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP,
    deleted_at   TIMESTAMP,
    name         VARCHAR(255) NOT NULL,
    generic_name VARCHAR(255),
    category     VARCHAR(100),
    form         VARCHAR(100),
    strength     VARCHAR(100),
    unit         VARCHAR(50),
    price        NUMERIC(10, 2),
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE prescription
(
    id                UUID PRIMARY KEY,
    created_at        TIMESTAMP   NOT NULL,
    updated_at        TIMESTAMP,
    deleted_at        TIMESTAMP,
    prescription_code VARCHAR(20) NOT NULL UNIQUE,
    medical_record_id UUID        NOT NULL,
    patient_id        UUID        NOT NULL,
    doctor_id         UUID        NOT NULL,
    prescription_date TIMESTAMP   NOT NULL,
    notes             TEXT,
    status            VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_prescription_medical_record FOREIGN KEY (medical_record_id) REFERENCES medical_record (id),
    CONSTRAINT fk_prescription_patient        FOREIGN KEY (patient_id)        REFERENCES patient_profile (id),
    CONSTRAINT fk_prescription_doctor         FOREIGN KEY (doctor_id)         REFERENCES doctor_profile (id)
);

CREATE TABLE prescription_item
(
    id              UUID PRIMARY KEY,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    prescription_id UUID      NOT NULL,
    medication_id   UUID      NOT NULL,
    dosage          VARCHAR(100),
    frequency       VARCHAR(100),
    duration        VARCHAR(100),
    quantity        INTEGER   NOT NULL,
    instructions    TEXT,
    CONSTRAINT fk_item_prescription FOREIGN KEY (prescription_id) REFERENCES prescription (id),
    CONSTRAINT fk_item_medication   FOREIGN KEY (medication_id)   REFERENCES medication (id)
);

CREATE TABLE invoice
(
    id                UUID PRIMARY KEY,
    created_at        TIMESTAMP      NOT NULL,
    updated_at        TIMESTAMP,
    deleted_at        TIMESTAMP,
    invoice_code      VARCHAR(20)    NOT NULL UNIQUE,
    appointment_id    UUID,
    patient_id        UUID           NOT NULL,
    invoice_date      TIMESTAMP      NOT NULL,
    subtotal          NUMERIC(10, 2) NOT NULL,
    discount_amount   NUMERIC(10, 2) NOT NULL DEFAULT 0,
    total_amount      NUMERIC(10, 2) NOT NULL,
    insurance_covered NUMERIC(10, 2) NOT NULL DEFAULT 0,
    patient_paid      NUMERIC(10, 2) NOT NULL DEFAULT 0,
    balance           NUMERIC(10, 2) NOT NULL DEFAULT 0,
    status            VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_invoice_appointment FOREIGN KEY (appointment_id) REFERENCES appointment (id),
    CONSTRAINT fk_invoice_patient     FOREIGN KEY (patient_id)     REFERENCES patient_profile (id)
);

CREATE TABLE invoice_item
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP      NOT NULL,
    updated_at  TIMESTAMP,
    deleted_at  TIMESTAMP,
    invoice_id  UUID           NOT NULL,
    item_type   VARCHAR(20)    NOT NULL,
    item_name   VARCHAR(255)   NOT NULL,
    quantity    INTEGER        NOT NULL DEFAULT 1,
    unit_price  NUMERIC(10, 2) NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_item_invoice FOREIGN KEY (invoice_id) REFERENCES invoice (id)
);

CREATE TABLE payment
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP      NOT NULL,
    updated_at     TIMESTAMP,
    deleted_at     TIMESTAMP,
    payment_code   VARCHAR(20)    NOT NULL UNIQUE,
    invoice_id     UUID           NOT NULL,
    patient_id     UUID           NOT NULL,
    amount         NUMERIC(10, 2) NOT NULL,
    payment_method VARCHAR(20)    NOT NULL,
    payment_date   TIMESTAMP      NOT NULL,
    status         VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_payment_invoice FOREIGN KEY (invoice_id) REFERENCES invoice (id),
    CONSTRAINT fk_payment_patient FOREIGN KEY (patient_id) REFERENCES patient_profile (id)
);

CREATE TABLE loyalty_transaction
(
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP   NOT NULL,
    updated_at       TIMESTAMP,
    deleted_at       TIMESTAMP,
    patient_id       UUID        NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    points           INTEGER     NOT NULL,
    reference_type   VARCHAR(50),
    reference_id     UUID,
    description      TEXT,
    balance_after    INTEGER     NOT NULL,
    expires_at       TIMESTAMP,
    CONSTRAINT fk_loyalty_patient FOREIGN KEY (patient_id) REFERENCES patient_profile (id)
);

CREATE TABLE promotion
(
    id                  UUID PRIMARY KEY,
    created_at          TIMESTAMP      NOT NULL,
    updated_at          TIMESTAMP,
    deleted_at          TIMESTAMP,
    code                VARCHAR(50)    NOT NULL UNIQUE,
    name                VARCHAR(255)   NOT NULL,
    description         TEXT,
    discount_type       VARCHAR(20)    NOT NULL,
    discount_value      NUMERIC(10, 2) NOT NULL,
    min_purchase_amount NUMERIC(10, 2) NOT NULL DEFAULT 0,
    max_discount_amount NUMERIC(10, 2),
    usage_limit         INTEGER,
    usage_count         INTEGER        NOT NULL DEFAULT 0,
    usage_per_user      INTEGER        NOT NULL DEFAULT 1,
    applicable_services JSON,
    start_date          TIMESTAMP      NOT NULL,
    end_date            TIMESTAMP      NOT NULL,
    is_active           BOOLEAN        NOT NULL DEFAULT TRUE
);

CREATE TABLE promotion_usage
(
    id              UUID PRIMARY KEY,
    created_at      TIMESTAMP      NOT NULL,
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    promotion_id    UUID           NOT NULL,
    user_id         UUID           NOT NULL,
    invoice_id      UUID,
    discount_amount NUMERIC(10, 2) NOT NULL,
    used_at         TIMESTAMP      NOT NULL,
    CONSTRAINT fk_usage_promotion FOREIGN KEY (promotion_id) REFERENCES promotion (id),
    CONSTRAINT fk_usage_user     FOREIGN KEY (user_id)      REFERENCES users (id),
    CONSTRAINT fk_usage_invoice  FOREIGN KEY (invoice_id)   REFERENCES invoice (id)
);

CREATE TABLE medical_equipment
(
    id                        UUID PRIMARY KEY,
    created_at                TIMESTAMP    NOT NULL,
    updated_at                TIMESTAMP,
    deleted_at                TIMESTAMP,
    equipment_code            VARCHAR(50)  NOT NULL UNIQUE,
    name                      VARCHAR(255) NOT NULL,
    category                  VARCHAR(100),
    manufacturer              VARCHAR(255),
    model                     VARCHAR(100),
    serial_number             VARCHAR(100),
    purchase_date             TIMESTAMP,
    purchase_price            NUMERIC(12, 2),
    warranty_expiry           TIMESTAMP,
    location                  VARCHAR(255),
    status                    VARCHAR(20)  NOT NULL DEFAULT 'OPERATIONAL',
    last_maintenance_date     TIMESTAMP,
    next_maintenance_date     TIMESTAMP,
    maintenance_interval_days INTEGER      NOT NULL DEFAULT 90,
    notes                     TEXT
);

CREATE TABLE inventory
(
    id              UUID PRIMARY KEY,
    created_at      TIMESTAMP   NOT NULL,
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    medication_id   UUID        NOT NULL,
    batch_number    VARCHAR(100),
    quantity        INTEGER     NOT NULL DEFAULT 0,
    expiry_date     TIMESTAMP,
    supplier        VARCHAR(255),
    status          VARCHAR(20) NOT NULL DEFAULT 'IN_STOCK',
    alert_threshold INTEGER     NOT NULL DEFAULT 10,
    CONSTRAINT fk_inventory_medication FOREIGN KEY (medication_id) REFERENCES medication (id)
);

CREATE TABLE equipment_maintenance
(
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP   NOT NULL,
    updated_at       TIMESTAMP,
    deleted_at       TIMESTAMP,
    equipment_id     UUID        NOT NULL,
    maintenance_type VARCHAR(20) NOT NULL,
    scheduled_date   TIMESTAMP,
    completed_date   TIMESTAMP,
    performed_by     VARCHAR(255),
    cost             NUMERIC(10, 2),
    description      TEXT,
    issues_found     TEXT,
    actions_taken    TEXT,
    status           VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    CONSTRAINT fk_maintenance_equipment FOREIGN KEY (equipment_id) REFERENCES medical_equipment (id)
);

CREATE TABLE revenue_report
(
    id                 UUID PRIMARY KEY,
    created_at         TIMESTAMP      NOT NULL,
    updated_at         TIMESTAMP,
    deleted_at         TIMESTAMP,
    report_type        VARCHAR(20)    NOT NULL,
    report_date        TIMESTAMP      NOT NULL,
    start_date         TIMESTAMP      NOT NULL,
    end_date           TIMESTAMP      NOT NULL,
    total_revenue      NUMERIC(12, 2) NOT NULL DEFAULT 0,
    service_revenue    NUMERIC(12, 2) NOT NULL DEFAULT 0,
    medication_revenue NUMERIC(12, 2) NOT NULL DEFAULT 0,
    lab_test_revenue   NUMERIC(12, 2) NOT NULL DEFAULT 0,
    total_appointments INTEGER        NOT NULL DEFAULT 0,
    total_patients     INTEGER        NOT NULL DEFAULT 0
);

CREATE TABLE daily_report
(
    id                     UUID PRIMARY KEY,
    created_at             TIMESTAMP      NOT NULL,
    updated_at             TIMESTAMP,
    deleted_at             TIMESTAMP,
    report_date            TIMESTAMP      NOT NULL,
    total_appointments     INTEGER        NOT NULL DEFAULT 0,
    completed_appointments INTEGER        NOT NULL DEFAULT 0,
    cancelled_appointments INTEGER        NOT NULL DEFAULT 0,
    new_patients           INTEGER        NOT NULL DEFAULT 0,
    returning_patients     INTEGER        NOT NULL DEFAULT 0,
    total_revenue          NUMERIC(12, 2) NOT NULL DEFAULT 0,
    cash_revenue           NUMERIC(12, 2) NOT NULL DEFAULT 0,
    online_revenue         NUMERIC(12, 2) NOT NULL DEFAULT 0,
    insurance_revenue      NUMERIC(12, 2) NOT NULL DEFAULT 0,
    pending_payments       NUMERIC(12, 2) NOT NULL DEFAULT 0,
    CONSTRAINT unique_report_date UNIQUE (report_date)
);

CREATE TABLE faq
(
    id            UUID PRIMARY KEY,
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP,
    deleted_at    TIMESTAMP,
    category      VARCHAR(100),
    question      TEXT      NOT NULL,
    answer        TEXT      NOT NULL,
    display_order INTEGER   NOT NULL DEFAULT 0,
    is_active     BOOLEAN   NOT NULL DEFAULT TRUE
);

CREATE TABLE password_reset_token
(
    id          UUID PRIMARY KEY,
    user_id     UUID         NOT NULL,
    token       VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP    NOT NULL,
    used        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL,
    CONSTRAINT fk_password_reset_token_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_phone ON users (phone);

CREATE INDEX idx_patients_user_id ON patient_profile (user_id);
CREATE INDEX idx_patients_patient_code ON patient_profile (patient_code);

CREATE INDEX idx_doctor_profile_user_id ON doctor_profile (user_id);
CREATE INDEX idx_doctor_profile_doctor_code ON doctor_profile (doctor_code);
CREATE INDEX idx_doctor_profile_specialty_id ON doctor_profile (specialty_id);
CREATE INDEX idx_doctor_profile_is_featured ON doctor_profile (is_featured);

CREATE INDEX idx_staff_profile_user_id ON staff_profile (user_id);
CREATE INDEX idx_staff_profile_staff_code ON staff_profile (staff_code);
CREATE INDEX idx_staff_profile_department ON staff_profile (department);

CREATE INDEX idx_specialties_slug ON specialty (slug);
CREATE INDEX idx_specialties_is_active ON specialty (is_active);

CREATE INDEX idx_services_slug ON services (slug);
CREATE INDEX idx_services_specialty_id ON services (specialty_id);
CREATE INDEX idx_services_is_featured ON services (is_featured);
CREATE INDEX idx_services_is_active ON services (is_active);

CREATE INDEX idx_appointments_appointment_code ON appointment (appointment_code);
CREATE INDEX idx_appointments_patient_id ON appointment (patient_id);
CREATE INDEX idx_appointments_doctor_id ON appointment (doctor_id);
CREATE INDEX idx_appointments_date ON appointment (appointment_date);
CREATE INDEX idx_appointments_status ON appointment (status);
CREATE INDEX idx_appointments_doctor_date_status ON appointment (doctor_id, appointment_date, status);
CREATE INDEX idx_appointments_patient_status_date ON appointment (patient_id, status, appointment_date);
CREATE INDEX idx_appointments_date_status ON appointment (appointment_date, status);

CREATE INDEX idx_doctor_schedule_exceptions_doctor_id ON doctor_schedule_exception (doctor_id);
CREATE INDEX idx_doctor_schedule_exceptions_date ON doctor_schedule_exception (exception_date);
CREATE INDEX idx_doctor_schedule_exceptions_doctor_date ON doctor_schedule_exception (doctor_id, exception_date);

CREATE INDEX idx_doctor_performance_doctor_id ON doctor_performance (doctor_id);
CREATE INDEX idx_doctor_performance_period ON doctor_performance (year, month);

CREATE INDEX idx_reviews_patient_id ON review (patient_id);
CREATE INDEX idx_reviews_doctor_id ON review (doctor_id);
CREATE INDEX idx_reviews_appointment_id ON review (appointment_id);
CREATE INDEX idx_reviews_rating ON review (rating);
CREATE INDEX idx_reviews_status ON review (status);

CREATE INDEX idx_medical_records_record_code ON medical_record (record_code);
CREATE INDEX idx_medical_records_appointment_id ON medical_record (appointment_id);
CREATE INDEX idx_medical_records_patient_id ON medical_record (patient_id);
CREATE INDEX idx_medical_records_doctor_id ON medical_record (doctor_id);
CREATE INDEX idx_medical_records_patient_created ON medical_record (patient_id, created_at);
CREATE INDEX idx_medical_records_doctor_created ON medical_record (doctor_id, created_at);

CREATE INDEX idx_prescriptions_code ON prescription (prescription_code);
CREATE INDEX idx_prescriptions_medical_record_id ON prescription (medical_record_id);
CREATE INDEX idx_prescriptions_patient_id ON prescription (patient_id);
CREATE INDEX idx_prescriptions_doctor_id ON prescription (doctor_id);
CREATE INDEX idx_prescriptions_status ON prescription (status);

CREATE INDEX idx_prescription_items_prescription_id ON prescription_item (prescription_id);
CREATE INDEX idx_prescription_items_medication_id ON prescription_item (medication_id);

CREATE INDEX idx_medications_name ON medication (name);
CREATE INDEX idx_medications_category ON medication (category);
CREATE INDEX idx_medications_is_active ON medication (is_active);

CREATE INDEX idx_invoices_code ON invoice (invoice_code);
CREATE INDEX idx_invoices_appointment_id ON invoice (appointment_id);
CREATE INDEX idx_invoices_patient_id ON invoice (patient_id);
CREATE INDEX idx_invoices_invoice_date ON invoice (invoice_date);
CREATE INDEX idx_invoices_status ON invoice (status);
CREATE INDEX idx_invoices_patient_status_date ON invoice (patient_id, status, invoice_date);
CREATE INDEX idx_invoices_date_status ON invoice (invoice_date, status);
CREATE INDEX idx_invoices_status_balance ON invoice (status, balance);

CREATE INDEX idx_invoice_item_invoice_id ON invoice_item (invoice_id);
CREATE INDEX idx_invoice_item_item_type ON invoice_item (item_type);

CREATE INDEX idx_payments_code ON payment (payment_code);
CREATE INDEX idx_payments_invoice_id ON payment (invoice_id);
CREATE INDEX idx_payments_patient_id ON payment (patient_id);
CREATE INDEX idx_payments_payment_date ON payment (payment_date);
CREATE INDEX idx_payments_status ON payment (status);
CREATE INDEX idx_payments_status_date ON payment (status, payment_date);
CREATE INDEX idx_payments_patient_status ON payment (patient_id, status);

CREATE INDEX idx_loyalty_transactions_patient_id ON loyalty_transaction (patient_id);
CREATE INDEX idx_loyalty_transactions_type ON loyalty_transaction (transaction_type);
CREATE INDEX idx_loyalty_transactions_created ON loyalty_transaction (created_at);

CREATE INDEX idx_promotions_code ON promotion (code);
CREATE INDEX idx_promotions_dates ON promotion (start_date, end_date);
CREATE INDEX idx_promotions_active ON promotion (is_active);

CREATE INDEX idx_promotion_usage_promotion_id ON promotion_usage (promotion_id);
CREATE INDEX idx_promotion_usage_user_id ON promotion_usage (user_id);
CREATE INDEX idx_promotion_usage_invoice_id ON promotion_usage (invoice_id);

CREATE INDEX idx_medical_equipment_code ON medical_equipment (equipment_code);
CREATE INDEX idx_medical_equipment_status ON medical_equipment (status);
CREATE INDEX idx_medical_equipment_next_maintenance ON medical_equipment (next_maintenance_date);

CREATE INDEX idx_inventory_medication_id ON inventory (medication_id);
CREATE INDEX idx_inventory_batch_number ON inventory (batch_number);
CREATE INDEX idx_inventory_status ON inventory (status);
CREATE INDEX idx_inventory_expiry_date ON inventory (expiry_date);

CREATE INDEX idx_equipment_maintenance_equipment ON equipment_maintenance (equipment_id);
CREATE INDEX idx_equipment_maintenance_scheduled ON equipment_maintenance (scheduled_date);
CREATE INDEX idx_equipment_maintenance_status ON equipment_maintenance (status);

CREATE INDEX idx_revenue_reports_report_date ON revenue_report (report_date);
CREATE INDEX idx_revenue_reports_report_type ON revenue_report (report_type);

CREATE INDEX idx_daily_reports_date ON daily_report (report_date);

CREATE INDEX idx_faqs_category ON faq (category);
CREATE INDEX idx_faqs_is_active ON faq (is_active);
CREATE INDEX idx_faqs_display_order ON faq (display_order);

CREATE INDEX idx_password_reset_tokens_token ON password_reset_token (token);
CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_token (user_id);