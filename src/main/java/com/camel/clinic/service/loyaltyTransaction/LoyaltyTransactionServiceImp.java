package com.camel.clinic.service.loyaltyTransaction;

import com.camel.clinic.dto.loyaltyTransaction.CreateLoyaltyTransactionDto;
import com.camel.clinic.dto.loyaltyTransaction.UpdateLoyaltyTransactionDto;
import com.camel.clinic.entity.LoyaltyTransaction;
import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.service.patientProfile.PatientProfileServiceInv;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class LoyaltyTransactionServiceImp implements LoyaltyTransactionService {
    private final LoyaltyTransactionServiceInv serviceInv;
    private final PatientProfileServiceInv patientProfileServiceInv;

    private static LoyaltyTransaction getLoyaltyTransaction(CreateLoyaltyTransactionDto requestBody, PatientProfile patientProfile) {
        LoyaltyTransaction loyaltyTransaction = new LoyaltyTransaction();
        loyaltyTransaction.setPatientProfile(patientProfile);
        loyaltyTransaction.setTransactionType(requestBody.getTransactionType());
        loyaltyTransaction.setPoints(requestBody.getPoints());
        loyaltyTransaction.setReferenceType(requestBody.getReferenceType());
        loyaltyTransaction.setReferenceId(requestBody.getReferenceId());
        loyaltyTransaction.setDescription(requestBody.getDescription());
        loyaltyTransaction.setExpiresAt(requestBody.getExpiresAt());
        return loyaltyTransaction;
    }

    @Override
    public ResponseEntity<?> calculateStatistics(Map<String, Object> queryParams) {
        return serviceInv.calculateStatistics(queryParams);
    }

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return serviceInv.list(queryParams);
    }

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateLoyaltyTransactionDto requestBody) {
        String patientProfileId = requestBody.getPatientProfileId();

        PatientProfile patientProfile = (PatientProfile) patientProfileServiceInv
                .retrieve(patientProfileId, null)
                .getBody();

        if (patientProfile == null) {
            throw new BadRequestException("Patient profile with ID " + patientProfileId + " not found");
        }
        LoyaltyTransaction loyaltyTransaction = getLoyaltyTransaction(requestBody, patientProfile);

        return serviceInv.create(loyaltyTransaction);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateLoyaltyTransactionDto requestBody) {
        LoyaltyTransaction loyaltyTransaction = serviceInv.retrieve(id, null).getBody() instanceof LoyaltyTransaction lt ? lt : null;
        if (loyaltyTransaction == null) {
            throw new IllegalArgumentException("LoyaltyTransaction with ID " + id + " not found");
        }
        loyaltyTransaction.setTransactionType(requestBody.getTransactionType());
        loyaltyTransaction.setPoints(requestBody.getPoints());
        loyaltyTransaction.setReferenceType(requestBody.getReferenceType());
        loyaltyTransaction.setReferenceId(requestBody.getReferenceId());
        loyaltyTransaction.setDescription(requestBody.getDescription());
        loyaltyTransaction.setExpiresAt(requestBody.getExpiresAt());

        return serviceInv.update(id, loyaltyTransaction, null);
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        return serviceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return serviceInv.restore(id);
    }
}
