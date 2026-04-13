package com.camel.clinic.processor.clinicSettings;

import com.camel.clinic.service.doctor.DoctorServiceImp;
import com.camel.clinic.service.patient.PatientServiceImp;
import com.camel.clinic.service.specialty.SpecialtyServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;


@Component("getClinicFiguresProcessor")
@AllArgsConstructor
public class GetClinicFiguresProcessor implements Processor {
    private final PatientServiceImp patientServiceImp;
    private final DoctorServiceImp doctorServiceImp;
    private final SpecialtyServiceImp specialtyServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, ?> result = Map.of(
                "patients", Objects.requireNonNull(patientServiceImp.countAllPatients().getBody()),
                "doctors", Objects.requireNonNull(doctorServiceImp.countAllDoctors().getBody()),
                "specialties", Objects.requireNonNull(specialtyServiceImp.countAllSpecialties().getBody())
        );

        exchange.getIn().setBody(result);

    }
}
