package com.camel.clinic.processor.doctorScheduleException;

import com.camel.clinic.dto.doctorScheduleException.CreateDoctorScheduleExceptionDto;
import com.camel.clinic.entity.DoctorScheduleException;
import com.camel.clinic.service.doctorScheduleException.DoctorScheduleExceptionServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("doctorScheduleExceptionBulkCreateProcessor")
@AllArgsConstructor
@Slf4j
public class DoctorScheduleExceptionBulkCreateProcessor implements Processor {
    private final DoctorScheduleExceptionServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String doctorProfileId = "40024a2e-8bd4-41cb-abbb-e46f08cc87b0";

        List<CreateDoctorScheduleExceptionDto> request = List.of(
                dto(doctorProfileId, "12/05/2026", DoctorScheduleException.ExceptionType.LEAVE, "Nghỉ phép cá nhân"),
                dto(doctorProfileId, "13/05/2026", DoctorScheduleException.ExceptionType.LEAVE, "Hội nghị y khoa"),
                dto(doctorProfileId, "14/05/2026", DoctorScheduleException.ExceptionType.LEAVE, "Khám sức khỏe định kỳ"),
                dto(doctorProfileId, "15/05/2026", DoctorScheduleException.ExceptionType.LEAVE, "Việc gia đình"),
                dto(doctorProfileId, "16/05/2026", DoctorScheduleException.ExceptionType.LEAVE, "Nghỉ bù"),
                dto(doctorProfileId, "19/05/2026", DoctorScheduleException.ExceptionType.LEAVE, "Tập huấn chuyên môn"),

                // EXTRA — T7 và CN
                dto(doctorProfileId, "07/06/2026", DoctorScheduleException.ExceptionType.EXTRA, "Hỗ trợ cuối tuần"),
                dto(doctorProfileId, "08/06/2026", DoctorScheduleException.ExceptionType.EXTRA, "Tăng ca theo yêu cầu"),
                dto(doctorProfileId, "14/06/2026", DoctorScheduleException.ExceptionType.EXTRA, "Bù lịch tuần trước"),
                dto(doctorProfileId, "15/06/2026", DoctorScheduleException.ExceptionType.EXTRA, "Hỗ trợ cuối tuần"),
                dto(doctorProfileId, "21/06/2026", DoctorScheduleException.ExceptionType.EXTRA, "Tăng ca theo yêu cầu"),
                dto(doctorProfileId, "22/06/2026", DoctorScheduleException.ExceptionType.EXTRA, "Hỗ trợ cuối tuần"),
                dto(doctorProfileId, "28/06/2026", DoctorScheduleException.ExceptionType.EXTRA, "Bù lịch tháng 5"),
                dto(doctorProfileId, "29/06/2026", DoctorScheduleException.ExceptionType.EXTRA, "Tăng ca theo yêu cầu"),
                dto(doctorProfileId, "16/06/2026", DoctorScheduleException.ExceptionType.EXTRA, "Hỗ trợ cuối tuần"),  // T2 nhưng test EXTRA
                dto(doctorProfileId, "20/06/2026", DoctorScheduleException.ExceptionType.LEAVE, "Nghỉ phép cuối tháng")
        );


        ResponseEntity<?> response = serviceImp.bulkCreate(request);
        exchange.getIn().setBody(response);
    }

    private CreateDoctorScheduleExceptionDto dto(
            String doctorProfileId,
            String date,
            DoctorScheduleException.ExceptionType type,
            String reason
    ) {
        CreateDoctorScheduleExceptionDto dto = new CreateDoctorScheduleExceptionDto();
        dto.setDoctorProfileId(doctorProfileId);
        dto.setExceptionDate(date);
        dto.setType(type);
        dto.setReason(reason);
        return dto;
    }
}