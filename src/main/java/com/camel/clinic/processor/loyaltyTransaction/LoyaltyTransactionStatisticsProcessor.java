package com.camel.clinic.processor.loyaltyTransaction;

import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.entity.Role;
import com.camel.clinic.repository.PatientProfileRepository;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.loyaltyTransaction.LoyaltyTransactionServiceImp;
import com.camel.clinic.util.JwtUtil;
import com.camel.clinic.util.SecuritiesUtils;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("loyaltyTransactionStatisticsProcessor")
@AllArgsConstructor
public class LoyaltyTransactionStatisticsProcessor implements Processor {
    private final LoyaltyTransactionServiceImp serviceImp;
    private final PatientProfileRepository patientProfileRepository;

    private final JwtUtil jwtUtil;

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        Role.RoleName role = SecuritiesUtils.getRole();

        String userId = jwtUtil.getUserIdFromToken(SecuritiesUtils.getAccessToken(exchange));
        if (role == Role.RoleName.PATIENT) {
            PatientProfile profile = patientProfileRepository.findByUserId(CommonService.parseToUuid(userId))
                    .orElseThrow(() -> new RuntimeException(
                            "Patient profile not found for user ID: " + userId));

            queryParams.put("patientProfileId", profile.getId().toString());

        } else {
            throw new RuntimeException("Only patients can access loyalty transaction statistics");
        }

        ResponseEntity<?> response = serviceImp.calculateStatistics(queryParams);

        exchange.getMessage().setBody(response);
    }
}
