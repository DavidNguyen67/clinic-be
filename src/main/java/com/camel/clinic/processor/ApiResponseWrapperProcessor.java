package com.camel.clinic.processor;

import com.camel.clinic.dto.api.ApiPaged;
import com.camel.clinic.dto.api.ApiResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("apiResponseWrapperProcessor")
public class ApiResponseWrapperProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        Object body = exchange.getMessage().getBody();

        // already wrapped
        if (body instanceof ApiResponse) {
            return;
        }

        // Some processors set ResponseEntity<?> as body
        if (body instanceof ResponseEntity<?> responseEntity) {
            Object inner = responseEntity.getBody();

            if (inner instanceof ApiResponse) {
                exchange.getMessage().setBody(inner);
                exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, responseEntity.getStatusCode().value());
                return;
            }

            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, responseEntity.getStatusCode().value());
            exchange.getMessage().setBody(ApiResponse.ok(normalizePagedIfLegacyMap(inner)));
            return;
        }

        exchange.getMessage().setBody(ApiResponse.ok(normalizePagedIfLegacyMap(body)));
    }

    /**
     * Backward-compat: if some code returns a legacy pagination map
     * {data,totalItems,page,size,totalPages} then convert to ApiPaged.
     */
    @SuppressWarnings("unchecked")
    private Object normalizePagedIfLegacyMap(Object body) {
        if (!(body instanceof Map<?, ?> map)) {
            return body;
        }
        if (map.containsKey("data") && (map.containsKey("totalItems") || map.containsKey("total"))) {
            Object itemsObj = map.get("data");
            if (itemsObj instanceof List<?> items) {
                long total = asLong(firstNonNull(map.get("totalItems"), map.get("total"), 0));
                int page = asInt(firstNonNull(map.get("page"), 0));
                int size = asInt(firstNonNull(map.get("size"), items.size()));
                int totalPages = asInt(firstNonNull(map.get("totalPages"), 0));
                return ApiPaged.of((List<Object>) items, total, page, size, totalPages);
            }
        }
        return body;
    }

    private Object firstNonNull(Object... values) {
        for (Object v : values) {
            if (v != null) return v;
        }
        return null;
    }

    private int asInt(Object v) {
        if (v == null) return 0;
        try {
            return Integer.parseInt(v.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private long asLong(Object v) {
        if (v == null) return 0;
        try {
            return Long.parseLong(v.toString());
        } catch (Exception e) {
            return 0;
        }
    }
}


