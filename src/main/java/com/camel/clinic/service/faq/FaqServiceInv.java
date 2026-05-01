package com.camel.clinic.service.faq;

import com.camel.clinic.entity.Faq;
import com.camel.clinic.repository.FaqRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FaqServiceInv extends BaseService<Faq, FaqRepository> {
    public FaqServiceInv(FaqRepository repository) {
        super(Faq::new, repository);
    }
}