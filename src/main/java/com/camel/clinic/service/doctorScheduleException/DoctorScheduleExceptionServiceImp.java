package com.camel.clinic.service.doctorScheduleException;

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
import java.util.Map;

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
        DoctorScheduleException doctorScheduleException = new DoctorScheduleException();
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
}
