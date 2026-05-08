package com.camel.clinic.service.invoice;

import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.dto.invoice.CreateInvoiceDto;
import com.camel.clinic.dto.invoice.UpdateInvoiceDto;
import com.camel.clinic.entity.Appointment;
import com.camel.clinic.entity.Invoice;
import com.camel.clinic.entity.InvoiceItem;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.repository.AppointmentRepository;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.invoiceItem.InvoiceItemServiceInv;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class InvoiceServiceImp implements InvoiceService {
    private final InvoiceServiceInv serviceInv;
    private final InvoiceItemServiceInv invoiceItemServiceInv;
    private final AppointmentRepository appointmentRepository;

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateInvoiceDto requestBody) {

        Appointment appointmentEntity = appointmentRepository
                .findById(UUID.fromString(requestBody.getAppointmentId()))
                .orElseThrow(() ->
                        new BadRequestException(
                                "Appointment with ID "
                                        + requestBody.getAppointmentId()
                                        + " not found"
                        )
                );

        // Chỉ cho tạo invoice khi appointment đang khám hoặc đã hoàn thành
        if (!EnumSet.of(
                Appointment.AppointmentStatus.PENDING
        ).contains(appointmentEntity.getStatus())) {

            throw new BadRequestException(
                    "Cannot create invoice for appointment with status: "
                            + appointmentEntity.getStatus()
            );
        }

        boolean existsInvoice = serviceInv.isExistInvoiceByAppointmentId(requestBody.getAppointmentId());

        if (existsInvoice) {
            throw new BadRequestException(
                    "Invoice already exists for this appointment"
            );
        }

        Invoice invoice = new Invoice();

        invoice.setInvoiceCode(CommonService.generateInvoiceCode());
        invoice.setAppointment(appointmentEntity);
        invoice.setPatientProfile(appointmentEntity.getPatientProfile());
        invoice.setInvoiceDate(requestBody.getInvoiceDate());
        invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
        invoice.setDiscountAmount(BigDecimal.ZERO);
        invoice.setInsuranceCovered(BigDecimal.ZERO);
        invoice.setPatientPaid(BigDecimal.ZERO);
        invoice.calculateTotals();

        return serviceInv.create(invoice);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateInvoiceDto requestBody, boolean isFromProcessor) {
        Invoice invoice = serviceInv.retrieve(id, null).getBody() instanceof Invoice inv ? inv : null;
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice with ID " + id + " not found");
        }

        invoice.setInvoiceDate(requestBody.getInvoiceDate());
        invoice.setStatus(requestBody.getStatus());
        invoice.setDiscountAmount(requestBody.getDiscountAmount());
        invoice.setInsuranceCovered(requestBody.getInsuranceCovered());
        invoice.setPatientPaid(requestBody.getPatientPaid());

        if (isFromProcessor) {
//            List<InvoiceItem> itemsList =
            Map<String, Object> params = Map.of("invoiceId", id);
            ResponseEntity<ApiPaged<?>> itemsPage =
                    (ResponseEntity<ApiPaged<?>>) invoiceItemServiceInv.list(params);
            List<InvoiceItem> itemsList = itemsPage.getBody() != null ? (List<InvoiceItem>) itemsPage.getBody().getData() : List.of();

            invoice.setItems(itemsList);
            invoice.calculateTotals();
            invoice.setItems(null);
        } else {
            List<InvoiceItem> itemsList = requestBody.getItems().stream()
                    .map(itemDto -> {
                        InvoiceItem item = new InvoiceItem();
                        item.setQuantity(itemDto.getQuantity());
                        item.setItemType(itemDto.getItemType());
                        item.setUnitPrice(itemDto.getUnitPrice());
                        item.setInvoice(invoice);
                        item.setItemName(itemDto.getItemName());
                        item.calculateTotalPrice();
                        return item;
                    })
                    .toList();

            invoiceItemServiceInv.bulkCreate(itemsList);

            invoice.setItems(itemsList);
            invoice.calculateTotals();
            invoice.setItems(null);
        }

        if (invoice.getBalance().equals(BigDecimal.ZERO) &&
                (EnumSet.of(Invoice.InvoiceStatus.DRAFT, Invoice.InvoiceStatus.PAID)
                        .contains(invoice.getStatus()))) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
        }

        return serviceInv.update(id, invoice, null);
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

