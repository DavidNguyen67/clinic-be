-- Initial schema generated from JPA entities in com.camel.clinic.entity

CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    email VARCHAR(255) NOT NULL UNIQUE,
    date_of_birth VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    path_avatar VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    last_login TIMESTAMP
);

CREATE TABLE specialties (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    image VARCHAR(500),
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    specialty_type VARCHAR(50) NOT NULL DEFAULT 'GENERAL'
);

CREATE TABLE services (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    specialty_id UUID,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    price NUMERIC(10, 2) NOT NULL,
    promotional_price NUMERIC(10, 2),
    duration INTEGER NOT NULL DEFAULT 30,
    image VARCHAR(500),
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_service_specialty FOREIGN KEY (specialty_id) REFERENCES specialties(id)
);

CREATE TABLE patients (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    user_id UUID NOT NULL UNIQUE,
    patient_code VARCHAR(20) NOT NULL UNIQUE,
    date_of_birth TIMESTAMP,
    gender VARCHAR(10),
    address TEXT,
    insurance_number VARCHAR(100),
    blood_type VARCHAR(5),
    allergies TEXT,
    chronic_diseases TEXT,
    loyalty_points INTEGER NOT NULL DEFAULT 0,
    total_visits INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_patient_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE doctors (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    user_id UUID NOT NULL UNIQUE,
    doctor_code VARCHAR(20) NOT NULL UNIQUE,
    specialty_id UUID NOT NULL,
    degree VARCHAR(100),
    experience_years INTEGER NOT NULL DEFAULT 0,
    education TEXT,
    bio TEXT,
    consultation_fee NUMERIC(10, 2) NOT NULL DEFAULT 0,
    average_rating NUMERIC(3, 2) NOT NULL DEFAULT 0,
    total_reviews INTEGER NOT NULL DEFAULT 0,
    total_patients INTEGER NOT NULL DEFAULT 0,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    CONSTRAINT fk_doctor_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_doctor_specialty FOREIGN KEY (specialty_id) REFERENCES specialties(id)
);

CREATE TABLE staff (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    user_id UUID NOT NULL UNIQUE,
    staff_code VARCHAR(20) NOT NULL UNIQUE,
    position VARCHAR(100),
    department VARCHAR(100),
    hire_date TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE appointments (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    appointment_code VARCHAR(20) NOT NULL UNIQUE,
    patient_id UUID NOT NULL,
    doctor_id UUID NOT NULL,
    service_id UUID,
    appointment_date TIMESTAMP NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    booking_type VARCHAR(20) NOT NULL DEFAULT 'online',
    reason TEXT,
    symptoms TEXT,
    notes TEXT,
    queue_number INTEGER,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    CONSTRAINT fk_appointment_service FOREIGN KEY (service_id) REFERENCES services(id)
);

CREATE TABLE doctor_schedules (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    doctor_id UUID NOT NULL,
    day_of_week INTEGER NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    slot_duration INTEGER NOT NULL DEFAULT 30,
    max_patients_per_slot INTEGER NOT NULL DEFAULT 1,
    location VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_schedule_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

CREATE TABLE doctor_leave (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    doctor_id UUID NOT NULL,
    leave_date TIMESTAMP NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    reason VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    CONSTRAINT fk_leave_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

CREATE TABLE doctor_performance (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    doctor_id UUID NOT NULL,
    month INTEGER NOT NULL,
    year INTEGER NOT NULL,
    total_appointments INTEGER NOT NULL DEFAULT 0,
    completed_appointments INTEGER NOT NULL DEFAULT 0,
    cancelled_appointments INTEGER NOT NULL DEFAULT 0,
    total_patients INTEGER NOT NULL DEFAULT 0,
    average_rating NUMERIC(3, 2),
    total_revenue NUMERIC(12, 2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_performance_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    CONSTRAINT unique_doctor_month UNIQUE (doctor_id, month, year)
);

CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    patient_id UUID NOT NULL,
    doctor_id UUID,
    appointment_id UUID,
    rating INTEGER NOT NULL,
    title VARCHAR(255),
    content TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    CONSTRAINT fk_review_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_review_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    CONSTRAINT fk_review_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

CREATE TABLE medical_records (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    record_code VARCHAR(20) NOT NULL UNIQUE,
    appointment_id UUID NOT NULL UNIQUE,
    patient_id UUID NOT NULL,
    doctor_id UUID NOT NULL,
    chief_complaint TEXT,
    vital_signs JSON,
    diagnosis TEXT NOT NULL,
    treatment_plan TEXT,
    follow_up_date TIMESTAMP,
    doctor_notes TEXT,
    CONSTRAINT fk_record_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    CONSTRAINT fk_record_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_record_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

CREATE TABLE medications (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    name VARCHAR(255) NOT NULL,
    generic_name VARCHAR(255),
    category VARCHAR(100),
    form VARCHAR(100),
    strength VARCHAR(100),
    unit VARCHAR(50),
    price NUMERIC(10, 2),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE prescriptions (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    prescription_code VARCHAR(20) NOT NULL UNIQUE,
    medical_record_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    doctor_id UUID NOT NULL,
    prescription_date TIMESTAMP NOT NULL,
    notes TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    CONSTRAINT fk_prescription_medical_record FOREIGN KEY (medical_record_id) REFERENCES medical_records(id),
    CONSTRAINT fk_prescription_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_prescription_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

CREATE TABLE prescription_items (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    prescription_id UUID NOT NULL,
    medication_id UUID NOT NULL,
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    duration VARCHAR(100),
    quantity INTEGER NOT NULL,
    instructions TEXT,
    CONSTRAINT fk_item_prescription FOREIGN KEY (prescription_id) REFERENCES prescriptions(id),
    CONSTRAINT fk_item_medication FOREIGN KEY (medication_id) REFERENCES medications(id)
);

CREATE TABLE invoices (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    invoice_code VARCHAR(20) NOT NULL UNIQUE,
    appointment_id UUID,
    patient_id UUID NOT NULL,
    invoice_date TIMESTAMP NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL,
    discount_amount NUMERIC(10, 2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(10, 2) NOT NULL,
    insurance_covered NUMERIC(10, 2) NOT NULL DEFAULT 0,
    patient_paid NUMERIC(10, 2) NOT NULL DEFAULT 0,
    balance NUMERIC(10, 2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    CONSTRAINT fk_invoice_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    CONSTRAINT fk_invoice_patient FOREIGN KEY (patient_id) REFERENCES patients(id)
);

CREATE TABLE invoice_items (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    invoice_id UUID NOT NULL,
    item_type VARCHAR(20) NOT NULL,
    item_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price NUMERIC(10, 2) NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_item_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

CREATE TABLE payments (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    payment_code VARCHAR(20) NOT NULL UNIQUE,
    invoice_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    CONSTRAINT fk_payment_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id),
    CONSTRAINT fk_payment_patient FOREIGN KEY (patient_id) REFERENCES patients(id)
);

CREATE TABLE loyalty_transactions (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    patient_id UUID NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    points INTEGER NOT NULL,
    reference_type VARCHAR(50),
    reference_id INTEGER,
    description TEXT,
    balance_after INTEGER NOT NULL,
    expires_at TIMESTAMP,
    CONSTRAINT fk_loyalty_patient FOREIGN KEY (patient_id) REFERENCES patients(id)
);

CREATE TABLE promotions (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    discount_type VARCHAR(20) NOT NULL,
    discount_value NUMERIC(10, 2) NOT NULL,
    min_purchase_amount NUMERIC(10, 2) NOT NULL DEFAULT 0,
    max_discount_amount NUMERIC(10, 2),
    usage_limit INTEGER,
    usage_count INTEGER NOT NULL DEFAULT 0,
    usage_per_user INTEGER NOT NULL DEFAULT 1,
    applicable_services JSON,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE promotion_usage (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    promotion_id UUID NOT NULL,
    user_id UUID NOT NULL,
    invoice_id UUID,
    discount_amount NUMERIC(10, 2) NOT NULL,
    used_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_usage_promotion FOREIGN KEY (promotion_id) REFERENCES promotions(id),
    CONSTRAINT fk_usage_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_usage_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

CREATE TABLE medical_equipment (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    equipment_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    manufacturer VARCHAR(255),
    model VARCHAR(100),
    serial_number VARCHAR(100),
    purchase_date TIMESTAMP,
    purchase_price NUMERIC(12, 2),
    warranty_expiry TIMESTAMP,
    location VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'operational',
    last_maintenance_date TIMESTAMP,
    next_maintenance_date TIMESTAMP,
    maintenance_interval_days INTEGER NOT NULL DEFAULT 90,
    notes TEXT
);

CREATE TABLE inventory (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    medication_id UUID NOT NULL,
    batch_number VARCHAR(100),
    quantity INTEGER NOT NULL DEFAULT 0,
    expiry_date TIMESTAMP,
    supplier VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'in_stock',
    alert_threshold INTEGER NOT NULL DEFAULT 10,
    CONSTRAINT fk_inventory_medication FOREIGN KEY (medication_id) REFERENCES medications(id)
);

CREATE TABLE equipment_maintenance (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    equipment_id UUID NOT NULL,
    maintenance_type VARCHAR(20) NOT NULL,
    scheduled_date TIMESTAMP,
    completed_date TIMESTAMP,
    performed_by VARCHAR(255),
    cost NUMERIC(10, 2),
    description TEXT,
    issues_found TEXT,
    actions_taken TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'scheduled',
    CONSTRAINT fk_maintenance_equipment FOREIGN KEY (equipment_id) REFERENCES medical_equipment(id)
);

CREATE TABLE revenue_reports (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    report_type VARCHAR(20) NOT NULL,
    report_date TIMESTAMP NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    total_revenue NUMERIC(12, 2) NOT NULL DEFAULT 0,
    service_revenue NUMERIC(12, 2) NOT NULL DEFAULT 0,
    medication_revenue NUMERIC(12, 2) NOT NULL DEFAULT 0,
    lab_test_revenue NUMERIC(12, 2) NOT NULL DEFAULT 0,
    total_appointments INTEGER NOT NULL DEFAULT 0,
    total_patients INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE daily_reports (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    report_date TIMESTAMP NOT NULL UNIQUE,
    total_appointments INTEGER NOT NULL DEFAULT 0,
    completed_appointments INTEGER NOT NULL DEFAULT 0,
    cancelled_appointments INTEGER NOT NULL DEFAULT 0,
    new_patients INTEGER NOT NULL DEFAULT 0,
    returning_patients INTEGER NOT NULL DEFAULT 0,
    total_revenue NUMERIC(12, 2) NOT NULL DEFAULT 0,
    cash_revenue NUMERIC(12, 2) NOT NULL DEFAULT 0,
    online_revenue NUMERIC(12, 2) NOT NULL DEFAULT 0,
    insurance_revenue NUMERIC(12, 2) NOT NULL DEFAULT 0,
    pending_payments NUMERIC(12, 2) NOT NULL DEFAULT 0,
    CONSTRAINT unique_report_date UNIQUE (report_date)
);

CREATE TABLE faqs (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    category VARCHAR(100),
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE password_reset_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_password_reset_token_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_phone ON users (phone);

CREATE INDEX idx_patients_user_id ON patients (user_id);
CREATE INDEX idx_patients_patient_code ON patients (patient_code);

CREATE INDEX idx_doctors_user_id ON doctors (user_id);
CREATE INDEX idx_doctors_doctor_code ON doctors (doctor_code);
CREATE INDEX idx_doctors_specialty_id ON doctors (specialty_id);
CREATE INDEX idx_doctors_is_featured ON doctors (is_featured);
CREATE INDEX idx_doctors_status ON doctors (status);

CREATE INDEX idx_staff_user_id ON staff (user_id);
CREATE INDEX idx_staff_staff_code ON staff (staff_code);
CREATE INDEX idx_staff_department ON staff (department);
CREATE INDEX idx_staff_status ON staff (status);

CREATE INDEX idx_specialties_slug ON specialties (slug);
CREATE INDEX idx_specialties_is_active ON specialties (is_active);

CREATE INDEX idx_services_slug ON services (slug);
CREATE INDEX idx_services_specialty_id ON services (specialty_id);
CREATE INDEX idx_services_is_featured ON services (is_featured);
CREATE INDEX idx_services_is_active ON services (is_active);

CREATE INDEX idx_appointments_appointment_code ON appointments (appointment_code);
CREATE INDEX idx_appointments_patient_id ON appointments (patient_id);
CREATE INDEX idx_appointments_doctor_id ON appointments (doctor_id);
CREATE INDEX idx_appointments_date ON appointments (appointment_date);
CREATE INDEX idx_appointments_status ON appointments (status);
CREATE INDEX idx_appointments_doctor_date_status ON appointments (doctor_id, appointment_date, status);
CREATE INDEX idx_appointments_patient_status_date ON appointments (patient_id, status, appointment_date);
CREATE INDEX idx_appointments_date_status ON appointments (appointment_date, status);

CREATE INDEX idx_doctor_schedules_doctor_id ON doctor_schedules (doctor_id);
CREATE INDEX idx_doctor_schedules_day_of_week ON doctor_schedules (day_of_week);
CREATE INDEX idx_doctor_schedules_is_active ON doctor_schedules (is_active);

CREATE INDEX idx_doctor_leave_doctor_id ON doctor_leave (doctor_id);
CREATE INDEX idx_doctor_leave_leave_date ON doctor_leave (leave_date);
CREATE INDEX idx_doctor_leave_status ON doctor_leave (status);

CREATE INDEX idx_doctor_performance_doctor_id ON doctor_performance (doctor_id);
CREATE INDEX idx_doctor_performance_period ON doctor_performance (year, month);

CREATE INDEX idx_reviews_patient_id ON reviews (patient_id);
CREATE INDEX idx_reviews_doctor_id ON reviews (doctor_id);
CREATE INDEX idx_reviews_appointment_id ON reviews (appointment_id);
CREATE INDEX idx_reviews_rating ON reviews (rating);
CREATE INDEX idx_reviews_status ON reviews (status);

CREATE INDEX idx_medical_records_record_code ON medical_records (record_code);
CREATE INDEX idx_medical_records_appointment_id ON medical_records (appointment_id);
CREATE INDEX idx_medical_records_patient_id ON medical_records (patient_id);
CREATE INDEX idx_medical_records_doctor_id ON medical_records (doctor_id);
CREATE INDEX idx_medical_records_patient_created ON medical_records (patient_id, created_at);
CREATE INDEX idx_medical_records_doctor_created ON medical_records (doctor_id, created_at);

CREATE INDEX idx_prescriptions_code ON prescriptions (prescription_code);
CREATE INDEX idx_prescriptions_medical_record_id ON prescriptions (medical_record_id);
CREATE INDEX idx_prescriptions_patient_id ON prescriptions (patient_id);
CREATE INDEX idx_prescriptions_doctor_id ON prescriptions (doctor_id);
CREATE INDEX idx_prescriptions_status ON prescriptions (status);

CREATE INDEX idx_prescription_items_prescription_id ON prescription_items (prescription_id);
CREATE INDEX idx_prescription_items_medication_id ON prescription_items (medication_id);

CREATE INDEX idx_medications_name ON medications (name);
CREATE INDEX idx_medications_category ON medications (category);
CREATE INDEX idx_medications_is_active ON medications (is_active);

CREATE INDEX idx_invoices_code ON invoices (invoice_code);
CREATE INDEX idx_invoices_appointment_id ON invoices (appointment_id);
CREATE INDEX idx_invoices_patient_id ON invoices (patient_id);
CREATE INDEX idx_invoices_invoice_date ON invoices (invoice_date);
CREATE INDEX idx_invoices_status ON invoices (status);
CREATE INDEX idx_invoices_patient_status_date ON invoices (patient_id, status, invoice_date);
CREATE INDEX idx_invoices_date_status ON invoices (invoice_date, status);
CREATE INDEX idx_invoices_status_balance ON invoices (status, balance);

CREATE INDEX idx_invoice_items_invoice_id ON invoice_items (invoice_id);
CREATE INDEX idx_invoice_items_item_type ON invoice_items (item_type);

CREATE INDEX idx_payments_code ON payments (payment_code);
CREATE INDEX idx_payments_invoice_id ON payments (invoice_id);
CREATE INDEX idx_payments_patient_id ON payments (patient_id);
CREATE INDEX idx_payments_payment_date ON payments (payment_date);
CREATE INDEX idx_payments_status ON payments (status);
CREATE INDEX idx_payments_status_date ON payments (status, payment_date);
CREATE INDEX idx_payments_patient_status ON payments (patient_id, status);

CREATE INDEX idx_loyalty_transactions_patient_id ON loyalty_transactions (patient_id);
CREATE INDEX idx_loyalty_transactions_type ON loyalty_transactions (transaction_type);
CREATE INDEX idx_loyalty_transactions_created ON loyalty_transactions (created_at);

CREATE INDEX idx_promotions_code ON promotions (code);
CREATE INDEX idx_promotions_dates ON promotions (start_date, end_date);
CREATE INDEX idx_promotions_active ON promotions (is_active);

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

CREATE INDEX idx_revenue_reports_report_date ON revenue_reports (report_date);
CREATE INDEX idx_revenue_reports_report_type ON revenue_reports (report_type);

CREATE INDEX idx_daily_reports_date ON daily_reports (report_date);

CREATE INDEX idx_faqs_category ON faqs (category);
CREATE INDEX idx_faqs_is_active ON faqs (is_active);
CREATE INDEX idx_faqs_display_order ON faqs (display_order);

CREATE INDEX idx_password_reset_tokens_token ON password_reset_tokens (token);
CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens (user_id);

