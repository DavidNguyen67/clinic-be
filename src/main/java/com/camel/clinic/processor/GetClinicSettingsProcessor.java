package com.camel.clinic.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

@Component("getClinicSettingsProcessor")
public class GetClinicSettingsProcessor implements Processor {

    private final ObjectMapper objectMapper;

    public GetClinicSettingsProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        ClassPathResource resource = new ClassPathResource("clinic-settings.json");
        InputStream in = resource.getInputStream();
        Map<String, Object> settings = objectMapper.readValue(in, new TypeReference<>() {});
        exchange.getIn().setBody(settings);
    }
}

