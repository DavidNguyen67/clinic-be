package com.camel.clinic.processor.schedule;

import com.camel.clinic.service.schedule.ScheduleServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("getAllSchedulesProcessor")
@AllArgsConstructor
@Slf4j
public class GetAllSchedulesProcessor implements Processor {
    private final ScheduleServiceImp scheduleServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();
        ResponseEntity<?> response = scheduleServiceImp.filterSchedules(queryParams);
        exchange.getMessage().setBody(response);
    }
}

