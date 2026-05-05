package com.camel.clinic.processor.appointment;

import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.repository.PatientProfileRepository;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.appointment.AppointmentServiceImp;
import com.camel.clinic.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component("appointmentListPatientProcessor")
@AllArgsConstructor
@Slf4j
public class AppointmentListPatientProcessor implements Processor {
    private final AppointmentServiceImp serviceImp;
    private final PatientProfileRepository patientProfileRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();
        String accessToken = CommonService.getAuthHeader(exchange);
        String userIdStr = jwtUtil.getUserIdFromToken(accessToken);
        UUID userId = CommonService.parseUuid(userIdStr);

        PatientProfile patientProfile = patientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Patient profile not found for user ID: " + userId));

        queryParams.put("patientProfileId", patientProfile.getId().toString());

        ResponseEntity<?> response = serviceImp.list(queryParams);

        exchange.getMessage().setBody(response);
    }
}