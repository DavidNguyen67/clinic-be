package com.camel.clinic.service.invoiceItem;

import com.camel.clinic.entity.InvoiceItem;
import com.camel.clinic.repository.InvoiceItemRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class InvoiceItemServiceInv extends BaseService<InvoiceItem, InvoiceItemRepository> {

    public InvoiceItemServiceInv(InvoiceItemRepository repository) {
        super(InvoiceItem::new, repository);
    }

    @Override
    protected Specification<InvoiceItem> buildSpec(Map<String, Object> queryParams) {
        return Specification.<InvoiceItem>unrestricted()
                .and(notDeleted())
                .and(multiFieldEquals(CommonService.parseToUuid(queryParams.get("invoiceId")),
                        new String[]{"invoice", "id"}
                ));
    }
}