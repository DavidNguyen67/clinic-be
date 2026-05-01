package com.camel.clinic.service.faq;

import com.camel.clinic.dto.faq.CreateFaqDto;
import com.camel.clinic.dto.faq.UpdateFaqDto;
import com.camel.clinic.entity.Faq;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class FaqServiceImp implements FaqService {
    private final FaqServiceInv serviceInv;

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateFaqDto requestBody) {
        Faq faq = new Faq();
        faq.setQuestion(requestBody.getQuestion());
        faq.setAnswer(requestBody.getAnswer());
        faq.setCategory(requestBody.getCategory());
        faq.setDisplayOrder(requestBody.getDisplayOrder());
        faq.setIsActive(requestBody.getIsActive());
        return serviceInv.create(faq);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateFaqDto requestBody) {
        Faq faq = serviceInv.retrieve(id, null).getBody() instanceof Faq f ? f : null;
        if (faq == null) {
            throw new IllegalArgumentException("Faq with ID " + id + " not found");
        }
        faq.setQuestion(requestBody.getQuestion());
        faq.setAnswer(requestBody.getAnswer());
        faq.setCategory(requestBody.getCategory());
        faq.setDisplayOrder(requestBody.getDisplayOrder());
        faq.setIsActive(requestBody.getIsActive());
        
        return serviceInv.update(id, faq, null);
    }

    @Override
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
