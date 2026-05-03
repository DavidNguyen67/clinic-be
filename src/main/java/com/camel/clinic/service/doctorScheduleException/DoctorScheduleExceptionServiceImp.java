package com.camel.clinic.service.doctorScheduleException;

import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.dto.doctorScheduleException.CreateDoctorScheduleExceptionDto;
import com.camel.clinic.dto.doctorScheduleException.UpdateDoctorScheduleExceptionDto;
import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.entity.DoctorScheduleException;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.doctorProfile.DoctorProfileServiceInv;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class DoctorScheduleExceptionServiceImp implements DoctorScheduleExceptionService {
    private final DoctorScheduleExceptionServiceInv serviceInv;
    private final CommonService commonService;
    private final DoctorProfileServiceInv doctorProfileServiceInv;

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateDoctorScheduleExceptionDto requestBody) {
        DoctorScheduleException doctorScheduleException = new DoctorScheduleException();
        Date exceptionDate = commonService.parseToDate(requestBody.getExceptionDate());

        List<DoctorScheduleException> existingExceptionsResponse = getCurrentDoctorScheduleException(requestBody.getDoctorProfileId(), exceptionDate);

        if (existingExceptionsResponse != null && !existingExceptionsResponse.isEmpty()) {
            throw new IllegalArgumentException("A DoctorScheduleException already exists for doctor profile ID " + requestBody.getDoctorProfileId() + " on date " + exceptionDate);
        }

        doctorScheduleException.setExceptionDate(exceptionDate);
        doctorScheduleException.setType(requestBody.getType());
        doctorScheduleException.setReason(requestBody.getReason());

        String doctorProfileId = requestBody.getDoctorProfileId();
        DoctorProfile doctorProfile = doctorProfileServiceInv.retrieve(doctorProfileId, null).getBody() instanceof DoctorProfile dp ? dp : null;
        if (doctorProfile == null) {
            throw new IllegalArgumentException("Doctor Profile with ID " + doctorProfileId + " not found");
        }
        doctorScheduleException.setDoctorProfile(doctorProfile);

        return serviceInv.create(doctorScheduleException);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateDoctorScheduleExceptionDto requestBody) {
        DoctorScheduleException doctorScheduleException = serviceInv.retrieve(id, null).getBody() instanceof DoctorScheduleException dse ? dse : null;
        if (doctorScheduleException == null) {
            throw new IllegalArgumentException("DoctorScheduleException with ID " + id + " not found");
        }
        Date exceptionDate = commonService.parseToDate(requestBody.getExceptionDate());
        doctorScheduleException.setExceptionDate(exceptionDate);
        doctorScheduleException.setType(requestBody.getType());
        doctorScheduleException.setReason(requestBody.getReason());

        return serviceInv.update(id, doctorScheduleException, null);
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

    private List<DoctorScheduleException> getCurrentDoctorScheduleException(String doctorProfileId, Date exceptionDate) {
        Map<String, Object> queryParams = Map.of(
                "doctorId", doctorProfileId,
                "exceptionDate", exceptionDate
        );
        ResponseEntity<?> response = serviceInv.list(queryParams);

        if (response.getStatusCode().is2xxSuccessful()) {
            ApiPaged<DoctorScheduleException> responseBody = (ApiPaged<DoctorScheduleException>) response.getBody();

            assert responseBody != null;
            return responseBody.getData().stream()
                    .filter(Objects::nonNull)
                    .toList();
        } else {
            throw new RuntimeException("Failed to retrieve DoctorScheduleException for doctor profile ID " + doctorProfileId + " and date " + exceptionDate + ": " + response.getBody());
        }
    }

}
