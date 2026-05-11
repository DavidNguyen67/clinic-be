package com.camel.clinic.service.loyaltyTransaction;

import com.camel.clinic.dto.loyaltyTransaction.CreateLoyaltyTransactionDto;
import com.camel.clinic.dto.loyaltyTransaction.UpdateLoyaltyTransactionDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface LoyaltyTransactionService {
    ResponseEntity<?> calculateStatistics(Map<String, Object> queryParams);
    
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> create(CreateLoyaltyTransactionDto requestBody);

    ResponseEntity<?> update(String id, UpdateLoyaltyTransactionDto requestBody);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}
