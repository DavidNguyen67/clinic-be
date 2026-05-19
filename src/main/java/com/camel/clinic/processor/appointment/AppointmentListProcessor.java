package com.camel.clinic.processor.appointment;

import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.entity.Role;
import com.camel.clinic.repository.DoctorProfileRepository;
import com.camel.clinic.repository.PatientProfileRepository;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.appointment.AppointmentServiceImp;
import com.camel.clinic.util.JwtUtil;
import com.camel.clinic.util.SecuritiesUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("appointmentListProcessor")
@AllArgsConstructor
@Slf4j
public class AppointmentListProcessor implements Processor {
    private final AppointmentServiceImp serviceImp;
    private final PatientProfileRepository patientProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    private final JwtUtil jwtUtil;

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        Role.RoleName role = SecuritiesUtils.getRole();

        String userId = jwtUtil.getUserIdFromToken(SecuritiesUtils.getAccessToken(exchange));
        if (role == Role.RoleName.PATIENT) {
            PatientProfile profile = patientProfileRepository.findByUserId(CommonService.parseToUuid(userId))
                    .orElseThrow(() -> new RuntimeException(
                            "Patient profile not found for user ID: " + userId));

            queryParams.put("patientProfileId", profile.getId().toString());

        }
        if (role == Role.RoleName.DOCTOR) {
            DoctorProfile doctorProfile = doctorProfileRepository.findByUserId(CommonService.parseToUuid(userId))
                    .orElseThrow(() -> new RuntimeException(
                            "DoctorProfile not found for user ID: " + userId));

            queryParams.put("doctorProfileId", doctorProfile.getId().toString());
        }

        ResponseEntity<?> response = serviceImp.list(queryParams);

        exchange.getMessage().setBody(response);
    }
}