package com.camel.clinic.service.invoice;

import com.camel.clinic.entity.Invoice;
import com.camel.clinic.repository.InvoiceRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class InvoiceServiceInv extends BaseService<Invoice, InvoiceRepository> {

    public InvoiceServiceInv(InvoiceRepository repository) {
        super(Invoice::new, repository);
    }

    @Override
    protected Specification<Invoice> buildSpec(Map<String, Object> queryParams) {
        return Specification.<Invoice>unrestricted()
                .and(notDeleted())
                .and((root, query, criteriaBuilder) -> {
                    if (queryParams.containsKey("appointmentId")) {
                        return criteriaBuilder.equal(
                                root.join("appointment", JoinType.INNER).get("id"),
                                queryParams.get("appointmentId")
                        );
                    }
                    return null;
                });
    }

    public boolean isExistInvoiceByAppointmentId(String appointmentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("appointmentId", CommonService.parseUuid(appointmentId));

        long count = repository.count(buildSpec(params));

        return count > 0;
    }
}