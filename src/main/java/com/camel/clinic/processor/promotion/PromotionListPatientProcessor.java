package com.camel.clinic.processor.promotion;

import com.camel.clinic.repository.PatientProfileRepository;
import com.camel.clinic.service.promotion.PromotionServiceImp;
import com.camel.clinic.util.JwtUtil;
import com.camel.clinic.util.SecuritiesUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("promotionListPatientProcessor")
@AllArgsConstructor
@Slf4j
public class PromotionListPatientProcessor implements Processor {
    private final PromotionServiceImp serviceImp;
    private final PatientProfileRepository patientProfileRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        SecuritiesUtils.injectPatientProfileId(queryParams, exchange, jwtUtil, patientProfileRepository);

        exchange.getMessage().setBody(serviceImp.list(queryParams));
    }
}