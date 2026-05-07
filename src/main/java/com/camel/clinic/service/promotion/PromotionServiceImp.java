package com.camel.clinic.service.promotion;

import com.camel.clinic.dto.promotion.CreatePromotionDto;
import com.camel.clinic.dto.promotion.UpdatePromotionDto;
import com.camel.clinic.entity.Promotion;
import com.camel.clinic.service.CommonService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class PromotionServiceImp implements PromotionService {
    private final PromotionServiceInv serviceInv;

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreatePromotionDto requestBody) {
        Promotion promotion = new Promotion();

        promotion.setCode(CommonService.generatePromotionCode());
        promotion.setName(requestBody.getName());
        promotion.setDescription(requestBody.getDescription());
        promotion.setDiscountType(requestBody.getDiscountType());
        promotion.setDiscountValue(requestBody.getDiscountValue());
        promotion.setMinPurchaseAmount(requestBody.getMinPurchaseAmount());
        promotion.setMaxDiscountAmount(requestBody.getMaxDiscountAmount());
        promotion.setUsageLimit(requestBody.getUsageLimit());
        promotion.setUsagePerUser(requestBody.getUsagePerUser());
        promotion.setApplicableServices(requestBody.getApplicableServices());
        promotion.setStartDate(requestBody.getStartDate());
        promotion.setEndDate(requestBody.getEndDate());

        return serviceInv.create(promotion);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdatePromotionDto requestBody) {
        Promotion promotion = serviceInv.retrieve(id, null).getBody() instanceof Promotion p ? p : null;
        if (promotion == null) {
            throw new IllegalArgumentException("Promotion with ID " + id + " not found");
        }

        promotion.setName(requestBody.getName());
        promotion.setDescription(requestBody.getDescription());
        promotion.setDiscountType(requestBody.getDiscountType());
        promotion.setDiscountValue(requestBody.getDiscountValue());
        promotion.setMinPurchaseAmount(requestBody.getMinPurchaseAmount());
        promotion.setMaxDiscountAmount(requestBody.getMaxDiscountAmount());
        promotion.setUsageLimit(requestBody.getUsageLimit());
        promotion.setUsagePerUser(requestBody.getUsagePerUser());
        promotion.setApplicableServices(requestBody.getApplicableServices());
        promotion.setStartDate(requestBody.getStartDate());
        promotion.setEndDate(requestBody.getEndDate());

        return serviceInv.update(id, promotion, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> delete(String id) {
        return serviceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return serviceInv.restore(id);
    }

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return serviceInv.list(queryParams);
    }
}
