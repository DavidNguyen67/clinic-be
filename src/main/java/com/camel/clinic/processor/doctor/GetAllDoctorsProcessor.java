package com.camel.clinic.processor.doctor;

import com.camel.clinic.service.doctor.DoctorServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component("getAllDoctorsProcessor")
@AllArgsConstructor
@Slf4j
public class GetAllDoctorsProcessor implements Processor {
    private final DoctorServiceImp doctorServiceImp;

    //    TODO: Bug filter doctor theo query params
    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();

        String queryString = exchange.getMessage().getHeader(
                Exchange.HTTP_QUERY, String.class);

        if (queryString != null) {
            Arrays.stream(queryString.split("&")).forEach(param -> {
                String[] kv = param.split("=", 2);
                if (kv.length == 2) {
                    try {
                        String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                        String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                        queryParams.put(key, value);
                    } catch (IllegalArgumentException e) {
                        // decode thất bại thì giữ nguyên raw value
                        log.warn("Failed to decode query param: {}={}", kv[0], kv[1]);
                        queryParams.put(kv[0], kv[1]);
                    }
                }
            });
        }

        ResponseEntity<?> response = doctorServiceImp.filterDoctors(queryParams);

        exchange.getMessage().setBody(response);
    }
}
