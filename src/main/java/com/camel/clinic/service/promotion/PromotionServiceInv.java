package com.camel.clinic.service.promotion;

import com.camel.clinic.entity.Promotion;
import com.camel.clinic.repository.PromotionRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PromotionServiceInv extends BaseService<Promotion, PromotionRepository> {

    public PromotionServiceInv(PromotionRepository repository) {
        super(Promotion::new, repository);
    }

}