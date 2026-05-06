package com.camel.clinic.dto.medicalRecord;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class UpdateMedicalRecordDto {
    private String patientProfileId;

    private String doctorProfileId;

    private String appointmentId;

    private String chiefComplaint;

    private Map<String, Object> vitalSigns;

    private String diagnosis;

    private String treatmentPlan;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    @Future(message = "FollowUp date must be in the future")
    private Date followUpDate;

    private String doctorNotes;
}