package com.camel.clinic.processor.doctor;

import com.camel.clinic.service.doctor.DoctorServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component("getAllDoctorsProcessor")
@AllArgsConstructor
public class GetAllDoctorsProcessor implements Processor {
    private final DoctorServiceImp doctorServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();

        String queryString = exchange.getMessage().getHeader(
                Exchange.HTTP_QUERY, String.class);

        if (queryString != null) {
            Arrays.stream(queryString.split("&")).forEach(param -> {
                String[] kv = param.split("=");
                if (kv.length == 2) {
                    queryParams.put(kv[0], kv[1]);
                }
            });
        }

        ResponseEntity<?> response = doctorServiceImp.filterDoctors(queryParams);

        exchange.getMessage().setBody(response);
    }
}
