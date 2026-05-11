package com.camel.clinic.service.loyaltyTransaction;

import com.camel.clinic.dto.loyaltyTransaction.LoyaltyTransactionStatisticsDto;
import com.camel.clinic.entity.LoyaltyTransaction;
import com.camel.clinic.repository.LoyaltyTransactionRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LoyaltyTransactionServiceInv extends BaseService<LoyaltyTransaction, LoyaltyTransactionRepository> {
    public LoyaltyTransactionServiceInv(LoyaltyTransactionRepository repository) {
        super(LoyaltyTransaction::new, repository);
    }

    public ResponseEntity<?> calculateStatistics(Map<String, Object> queryParams) {
        LoyaltyTransactionStatisticsDto statistics = LoyaltyTransactionStatisticsDto.from(
                countByTransactionType(LoyaltyTransaction.TransactionType.EARN, queryParams),
                countByTransactionType(LoyaltyTransaction.TransactionType.REDEEM, queryParams),
                countByTransactionType(LoyaltyTransaction.TransactionType.EXPIRE, queryParams)
        );

        return ResponseEntity.ok(statistics);
    }

    private long countByTransactionType(LoyaltyTransaction.TransactionType transactionType, Map<String, Object> baseParams) {
        Map<String, Object> params = new HashMap<>(baseParams);
        params.put("transactionType", transactionType.name());
        return repository.count(buildSpec(params));
    }


    @Override
    protected Specification<LoyaltyTransaction> buildSpec(Map<String, Object> queryParams) {
        return Specification.<LoyaltyTransaction>unrestricted()
                .and(notDeleted())
                .and(multiFieldLike((String) queryParams.get("fullName"),
                                new String[]{"patientProfile", "user", "fullName"}
                        )
                                .and(multiFieldEquals(queryParams.get("patientProfileId"),
                                        new String[]{"patientProfile", "id"}
                                ))
                                .and(multiFieldIn(
                                        parseEnumList(queryParams.get("transactionType"), LoyaltyTransaction.TransactionType.class),
                                        new String[]{"transactionType"}
                                ))
                );
    }
}