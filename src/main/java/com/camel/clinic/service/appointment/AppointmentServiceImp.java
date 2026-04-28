package com.camel.clinic.service.appointment;

import com.camel.clinic.dto.appointment.AppointmentCancelRequestDTO;
import com.camel.clinic.dto.appointment.AppointmentCreateRequestDTO;
import com.camel.clinic.dto.appointment.AppointmentResponseDTO;
import com.camel.clinic.entity.*;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.exception.NotFoundException;
import com.camel.clinic.exception.UnauthorizedException;
import com.camel.clinic.repository.*;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImp implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorLeaveRepository doctorLeaveRepository;
    private final ServiceRepository serviceRepository;
    private final SlotLockService slotLockService;
    private final AppointmentServiceInv appointmentServiceInv;
    private final SpecialtyRepository specialtyRepository;
    private final CommonService commonService;

    public ResponseEntity<?> createAppointment(AppointmentCreateRequestDTO dto) {
        try {
            User currentUser = commonService.getCurrentUser();
            commonService.requireRole(currentUser, Role.RoleName.PATIENT.name());

            Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new NotFoundException("Doctor not found"));
            if (doctor.getStatus() != Doctor.DoctorStatus.active) {
                throw new BadRequestException("Doctor is not active");
            }

            Patient patient = patientRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NotFoundException("Patient profile not found"));

            ClinicService clinicService = serviceRepository.findById(dto.getServiceId())
                    .orElseThrow(() -> new NotFoundException("Service not found"));

            Specialty specialty = specialtyRepository.findById(dto.getSpecialtyId())
                    .orElseThrow(() -> new NotFoundException("Specialty not found"));

            LocalDate apptDate = DateTimeUtils.toVnLocalDate(dto.getDate());
            LocalTime apptTime = DateTimeUtils.toVnLocalTime(dto.getTime());
            LocalDateTime appointmentDateTime = LocalDateTime.of(apptDate, apptTime);
            if (appointmentDateTime.isBefore(LocalDateTime.now(DateTimeUtils.VN_ZONE).plusMinutes(1))) {
                throw new BadRequestException("Appointment time must be in the future");
            }

            if (!hasValidScheduleSlot(doctor.getId(), apptDate, apptTime)) {
                throw new BadRequestException("Doctor does not have a valid schedule for this slot");
            }

            LocalTime slotEndTime = apptTime.plusMinutes(clinicService.getDuration());
            if (isOnApprovedLeave(doctor.getId(), dto.getDate(), apptTime, slotEndTime)) {
                throw new BadRequestException("Doctor is on approved leave for this time slot");
            }

            if (Boolean.FALSE.equals(clinicService.getIsActive())) {
                throw new BadRequestException("Service is not active");
            }

            Date startTime = DateTimeUtils.toDate(apptDate, apptTime);

            boolean occupied = appointmentRepository.existsByDoctorIdAndAppointmentDateAndStartTimeAndStatusInAndDeletedAtIsNull(
                    doctor.getId(),
                    dto.getDate(),
                    startTime,
                    List.of(
                            Appointment.AppointmentStatus.pending,
                            Appointment.AppointmentStatus.confirmed,
                            Appointment.AppointmentStatus.checked_in,
                            Appointment.AppointmentStatus.in_progress
                    )
            );
            if (occupied) {
                throw new BadRequestException("Slot already booked");
            }

            UUID lockRef = UUID.randomUUID();
            if (!slotLockService.tryLock(doctor.getId(), apptDate, apptTime, lockRef)) {
                throw new BadRequestException("Slot is being booked by another request");
            }

            try {
                boolean occupiedAfterLock = appointmentRepository.existsByDoctorIdAndAppointmentDateAndStartTimeAndStatusInAndDeletedAtIsNull(
                        doctor.getId(),
                        dto.getDate(),
                        startTime,
                        List.of(
                                Appointment.AppointmentStatus.pending,
                                Appointment.AppointmentStatus.confirmed,
                                Appointment.AppointmentStatus.checked_in,
                                Appointment.AppointmentStatus.in_progress
                        )
                );
                if (occupiedAfterLock) {
                    throw new BadRequestException("Slot already booked");
                }

                Appointment appointment = new Appointment();
                appointment.setAppointmentCode("APT" + System.currentTimeMillis());
                appointment.setDoctor(doctor);
                appointment.setPatient(patient);
                appointment.setClinicService(clinicService);
                appointment.setSpecialty(specialty);
                appointment.setAppointmentDate(dto.getDate());
                appointment.setStartTime(startTime);
                appointment.setEndTime(DateTimeUtils.toDate(apptDate, apptTime.plusMinutes(clinicService.getDuration())));
                appointment.setReason(dto.getReason());
                appointment.setSymptoms(dto.getSymptoms());
                appointment.setStatus(Appointment.AppointmentStatus.pending);
                appointment.setBookingType(parseBookingType(dto.getServiceType()));

                Appointment saved = appointmentRepository.save(appointment);
                // TODO: Send appointment confirmation email to patient after booking succeeds.
                AppointmentResponseDTO responseDTO = toDto(saved);
                List<String> instructions = List.of(
                        "Vui long den truoc gio hen 15 phut de lam thu tuc.",
                        "Mang theo CCCD/BHYT va cac ket qua kham gan nhat (neu co).",
                        "Neu can huy, vui long huy truoc it nhat 2 gio."
                );
                URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(saved.getId())
                        .toUri();


                return ResponseEntity.created(location).body(Map.of("appointment", responseDTO, "instructions", instructions));
            } finally {
                slotLockService.releaseLock(doctor.getId(), apptDate, apptTime);
            }
        } catch (NotFoundException | BadRequestException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Create appointment error", e);
            throw new RuntimeException("Failed to create appointment", e);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> listAppointments(Map<String, Object> queryParams) {
        return appointmentServiceInv.listAppointments(queryParams);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getAppointmentDetail(String id) {
        try {
            User currentUser = commonService.getCurrentUser();
            Appointment appointment = appointmentRepository.findById(UUID.fromString(id))
                    .orElseThrow(() -> new NotFoundException("Appointment not found"));
            ensureCanAccessAppointment(currentUser, appointment);
            return ResponseEntity.ok(toDto(appointment));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid appointment id");
        } catch (NotFoundException e) {
            throw e;
        }
    }

    public ResponseEntity<?> cancelAppointment(String id, AppointmentCancelRequestDTO dto) {
        try {
            Appointment appointment = appointmentRepository.findById(UUID.fromString(id))
                    .orElseThrow(() -> new NotFoundException("Appointment not found"));
            User currentUser = commonService.getCurrentUser();

            boolean patientRole = Role.RoleName.PATIENT.name().equals(currentUser.getRole().name());
            boolean staffRole = Role.RoleName.STAFF.name().equals(currentUser.getRole().name());
            if (!patientRole && !staffRole) {
                throw new UnauthorizedException("Only patient/staff can cancel appointment");
            }

            if (patientRole) {
                if (!(appointment.getStatus() == Appointment.AppointmentStatus.pending
                        || appointment.getStatus() == Appointment.AppointmentStatus.confirmed)) {
                    throw new BadRequestException("Patient can only cancel pending/confirmed appointments");
                }
                Patient patient = patientRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new NotFoundException("Patient profile not found"));
                if (!appointment.getPatient().getId().equals(patient.getId())) {
                    throw new UnauthorizedException("Cannot cancel another patient's appointment");
                }

                LocalDateTime appt = LocalDateTime.of(DateTimeUtils.toVnLocalDate(appointment.getAppointmentDate()),
                        DateTimeUtils.toVnLocalTime(appointment.getStartTime()));
                long hours = java.time.Duration.between(LocalDateTime.now(DateTimeUtils.VN_ZONE), appt).toHours();
                if (hours < 2) {
                    String note = appointment.getNotes() == null ? "" : appointment.getNotes() + " | ";
                    appointment.setNotes(note + "CANCEL_FEE_POSSIBLE");
                }
            }

            if (staffRole && (dto.getReason() == null || dto.getReason().isBlank())) {
                throw new BadRequestException("Staff must provide cancel reason");
            }

            appointment.setStatus(Appointment.AppointmentStatus.cancelled);
            String reason = dto != null ? dto.getReason() : null;
            String reasonNote = (reason == null || reason.isBlank()) ? "CANCELLED_BY_PATIENT" : "CANCEL_REASON:" + reason;
            String note = appointment.getNotes() == null ? "" : appointment.getNotes() + " | ";
            appointment.setNotes(note + reasonNote);
            return ResponseEntity.ok(toDto(appointmentRepository.save(appointment)));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid appointment id");
        } catch (NotFoundException | BadRequestException | UnauthorizedException e) {
            throw e;
        }
    }

    public ResponseEntity<?> confirmAppointment(String id) {
        return transitionStatus(id, Appointment.AppointmentStatus.pending, Appointment.AppointmentStatus.confirmed,
                Role.RoleName.STAFF.name());
    }

    public ResponseEntity<?> checkinAppointment(String id) {
        try {
            User currentUser = commonService.getCurrentUser();
            commonService.requireRole(currentUser, Role.RoleName.STAFF.name());
            Appointment appointment = appointmentRepository.findById(UUID.fromString(id))
                    .orElseThrow(() -> new NotFoundException("Appointment not found"));

            if (appointment.getStatus() != Appointment.AppointmentStatus.confirmed) {
                throw new BadRequestException("Only confirmed appointment can be checked in");
            }
            appointment.setStatus(Appointment.AppointmentStatus.checked_in);
            return ResponseEntity.ok(toDto(appointmentRepository.save(appointment)));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid appointment id");
        } catch (Exception e) {
            if (e instanceof NotFoundException || e instanceof BadRequestException || e instanceof UnauthorizedException) {
                throw (RuntimeException) e;
            }
            log.error("Appointment transition error", e);
            throw new RuntimeException("Failed to update appointment status", e);
        }
    }

    public ResponseEntity<?> startAppointment(String id) {
        return transitionStatus(id, Appointment.AppointmentStatus.checked_in, Appointment.AppointmentStatus.in_progress,
                Role.RoleName.DOCTOR.name());
    }

    public ResponseEntity<?> completeAppointment(String id) {
        return transitionStatus(id, Appointment.AppointmentStatus.in_progress, Appointment.AppointmentStatus.completed,
                Role.RoleName.DOCTOR.name());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getTodayAppointments() {
        try {
            User currentUser = commonService.getCurrentUser();
            LocalDate today = DateTimeUtils.todayVn();
            Date from = DateTimeUtils.toDate(today, LocalTime.MIN);
            Date to = DateTimeUtils.toDate(today.plusDays(1), LocalTime.MIN);

            List<Appointment> appointments;
            if (Role.RoleName.DOCTOR.name().equals(currentUser.getRole().name())) {
                Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new NotFoundException("Doctor profile not found"));
                appointments = appointmentRepository.findTodayAppointmentsByDoctor(doctor.getId(), from, to);
            } else if (Role.RoleName.STAFF.name().equals(currentUser.getRole().name())) {
                appointments = appointmentRepository.findTodayAppointments(from, to);
            } else {
                throw new UnauthorizedException("Only doctor/staff can view today appointments");
            }
            return ResponseEntity.ok(appointments.stream().map(this::toDto).collect(Collectors.toList()));
        } catch (NotFoundException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Today appointments error", e);
            throw new RuntimeException("Failed to load today's appointments", e);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getQueueAppointments() {
        try {
            User currentUser = commonService.getCurrentUser();
            commonService.requireRole(currentUser, Role.RoleName.STAFF.name());
            List<AppointmentResponseDTO> queue = appointmentRepository.findQueueAppointments()
                    .stream().map(this::toDto).collect(Collectors.toList());
            return ResponseEntity.ok(queue);
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Queue appointments error", e);
            throw new RuntimeException("Failed to load queue", e);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getStaffAppointments(Map<String, Object> queryParams) {
        try {
            User currentUser = commonService.getCurrentUser();
            commonService.requireRole(currentUser, Role.RoleName.STAFF.name());

            int page = commonService.parseIntParam(queryParams, "page", 0);
            int size = commonService.parseIntParam(queryParams, "size", 20);
            String patientName = commonService.getStringParam(queryParams, "patientName");
            Date dateParam;
            LocalDate localDate;
            try {
                localDate = commonService.parseToLocalDate((String) queryParams.get("date"));
                dateParam = Date.from(
                        localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                );
            } catch (Exception e) {
                log.error("Invalid query params: {}", queryParams, e);
                throw new IllegalArgumentException("Invalid doctorId or date format");
            }

            Pageable pageable = PageRequest.of(page, size);

            Page<Appointment> resultPage = appointmentRepository.findStaffAppointments(patientName, dateParam, pageable);
            return ResponseEntity.ok(commonService.buildPageResponse(resultPage));
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Staff appointments error", e);
            throw new RuntimeException("Failed to load staff appointments", e);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getStaffAppointmentStats(Map<String, Object> queryParams) {
        try {
            User currentUser = commonService.getCurrentUser();
            commonService.requireRole(currentUser, Role.RoleName.STAFF.name());

            Date dateParam;
            LocalDate localDate;
            try {
                localDate = commonService.parseToLocalDate((String) queryParams.get("date"));
                dateParam = Date.from(
                        localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                );
            } catch (Exception e) {
                log.error("Invalid query params: {}", queryParams, e);
                throw new IllegalArgumentException("Invalid doctorId or date format");
            }

            long totalAppointments = appointmentRepository.countByAppointmentDateAndDeletedAtIsNull(dateParam);
            long pendingAppointments = appointmentRepository.countStaffAppointmentsByStatus(Appointment.AppointmentStatus.pending, dateParam);
            long confirmedAppointments = appointmentRepository.countStaffAppointmentsByStatus(Appointment.AppointmentStatus.confirmed, dateParam);
            long checkedInAppointments = appointmentRepository.countStaffAppointmentsByStatus(Appointment.AppointmentStatus.checked_in, dateParam);
            long inProgressAppointments = appointmentRepository.countStaffAppointmentsByStatus(Appointment.AppointmentStatus.in_progress, dateParam);
            long completedAppointments = appointmentRepository.countStaffAppointmentsByStatus(Appointment.AppointmentStatus.completed, dateParam);
            long cancelledAppointments = appointmentRepository.countStaffAppointmentsByStatus(Appointment.AppointmentStatus.cancelled, dateParam);
            long otherAppointments = totalAppointments - pendingAppointments - confirmedAppointments - checkedInAppointments
                    - inProgressAppointments - completedAppointments - cancelledAppointments;

            Map<String, Long> stats = Map.of(
                    "total", totalAppointments,
                    "pending", pendingAppointments,
                    "confirmed", confirmedAppointments,
                    "checked_in", checkedInAppointments,
                    "in_progress", inProgressAppointments,
                    "completed", completedAppointments,
                    "cancelled", cancelledAppointments,
                    "other", otherAppointments
            );

            return ResponseEntity.ok(stats);
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Staff appointment stats error", e);
            throw new RuntimeException("Failed to load staff appointment stats", e);
        }
    }

    private ResponseEntity<?> transitionStatus(String id,
                                               Appointment.AppointmentStatus from,
                                               Appointment.AppointmentStatus to,
                                               String requiredRole) {
        try {
            User currentUser = commonService.getCurrentUser();
            commonService.requireRole(currentUser, requiredRole);
            Appointment appointment = appointmentRepository.findById(UUID.fromString(id))
                    .orElseThrow(() -> new NotFoundException("Appointment not found"));
            if (Role.RoleName.DOCTOR.name().equals(requiredRole)
                    && !appointment.getDoctor().getUser().getId().equals(currentUser.getId())) {
                throw new UnauthorizedException("Cannot update another doctor's appointment");
            }
            if (appointment.getStatus() != from) {
                throw new BadRequestException("Invalid status transition: " + appointment.getStatus() + " -> " + to);
            }
            appointment.setStatus(to);
            return ResponseEntity.ok(toDto(appointmentRepository.save(appointment)));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid appointment id");
        } catch (Exception e) {
            if (e instanceof NotFoundException || e instanceof BadRequestException || e instanceof UnauthorizedException) {
                throw (RuntimeException) e;
            }
            log.error("Appointment transition error", e);
            throw new RuntimeException("Failed to update appointment status", e);
        }
    }

    private Appointment.BookingType parseBookingType(String serviceType) {
        if (serviceType == null) return Appointment.BookingType.online;
        String normalized = serviceType.trim().toLowerCase();
        return switch (normalized) {
            case "phone" -> Appointment.BookingType.phone;
            case "walk_in", "walkin" -> Appointment.BookingType.walk_in;
            default -> Appointment.BookingType.online;
        };
    }

    private boolean isOnApprovedLeave(UUID doctorId, Date leaveDate, LocalTime slotStart, LocalTime slotEnd) {
        List<DoctorLeave> approvedLeaves = doctorLeaveRepository.findApprovedByDoctorIdAndLeaveDate(doctorId, leaveDate);
        return approvedLeaves.stream().anyMatch(leave -> {
            if (leave.getStartTime() == null || leave.getEndTime() == null) {
                return true;
            }

            LocalTime leaveStart = DateTimeUtils.toVnLocalTime(leave.getStartTime()).truncatedTo(ChronoUnit.MINUTES);
            LocalTime leaveEnd = DateTimeUtils.toVnLocalTime(leave.getEndTime()).truncatedTo(ChronoUnit.MINUTES);
            LocalTime reqStart = slotStart.truncatedTo(ChronoUnit.MINUTES);
            LocalTime reqEnd = slotEnd.truncatedTo(ChronoUnit.MINUTES);
            return reqStart.isBefore(leaveEnd) && reqEnd.isAfter(leaveStart);
        });
    }

    private boolean hasValidScheduleSlot(UUID doctorId, LocalDate date, LocalTime time) {
        int dayOfWeek = date.getDayOfWeek().getValue() % 7;
        return doctorScheduleRepository.findActiveByDoctorIdAndDayOfWeek(doctorId, dayOfWeek)
                .stream()
                .anyMatch(s -> {
                    LocalTime start = DateTimeUtils.toVnLocalTime(s.getStartTime()).truncatedTo(ChronoUnit.MINUTES);
                    LocalTime end = DateTimeUtils.toVnLocalTime(s.getEndTime()).truncatedTo(ChronoUnit.MINUTES);
                    LocalTime requestTime = time.truncatedTo(ChronoUnit.MINUTES);
                    if (requestTime.isBefore(start) || !requestTime.isBefore(end)) {
                        return false;
                    }
                    int minutesFromStart = (int) ChronoUnit.MINUTES.between(start, requestTime);
                    return minutesFromStart % Math.max(1, s.getSlotDuration()) == 0;
                });
    }

    private void ensureCanAccessAppointment(User currentUser, Appointment appointment) {
        String role = currentUser.getRole().name();
        if (Role.RoleName.ADMIN.name().equals(role) || Role.RoleName.STAFF.name().equals(role)) {
            return;
        }
        if (Role.RoleName.PATIENT.name().equals(role)) {
            Patient patient = patientRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NotFoundException("Patient profile not found"));
            if (!appointment.getPatient().getId().equals(patient.getId())) {
                throw new UnauthorizedException("Cannot access another patient's appointment");
            }
            return;
        }
        if (Role.RoleName.DOCTOR.name().equals(role)) {
            Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NotFoundException("Doctor profile not found"));
            if (!appointment.getDoctor().getId().equals(doctor.getId())) {
                throw new UnauthorizedException("Cannot access another doctor's appointment");
            }
            return;
        }
        throw new UnauthorizedException("Role not allowed to access appointment detail");
    }

    private AppointmentResponseDTO toDto(Appointment a) {
        return AppointmentResponseDTO.builder()
                .id(a.getId())
                .appointmentCode(a.getAppointmentCode())
                .appointmentDate(a.getAppointmentDate())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .status(a.getStatus().name())
                .bookingType(a.getBookingType().name())
                .reason(a.getReason())
                .symptoms(a.getSymptoms())
                .notes(a.getNotes())
                .queueNumber(a.getQueueNumber())
                .doctorId(a.getDoctor() != null ? a.getDoctor().getId() : null)
                .doctorName(a.getDoctor() != null && a.getDoctor().getUser() != null ? a.getDoctor().getUser().getFullName() : null)
                .patientId(a.getPatient() != null ? a.getPatient().getId() : null)
                .patientName(a.getPatient() != null && a.getPatient().getUser() != null ? a.getPatient().getUser().getFullName() : null)
                .serviceId(a.getClinicService() != null ? a.getClinicService().getId() : null)
                .serviceName(a.getClinicService() != null ? a.getClinicService().getName() : null)
                .patientPhone(a.getPatient() != null && a.getPatient().getUser() != null ? a.getPatient().getUser().getPhone() : null)
                .patientEmail(a.getPatient() != null && a.getPatient().getUser() != null ? a.getPatient().getUser().getEmail() : null)
                .doctorPhone(a.getDoctor() != null && a.getDoctor().getUser() != null ? a.getDoctor().getUser().getPhone() : null)
                .doctorEmail(a.getDoctor() != null && a.getDoctor().getUser() != null ? a.getDoctor().getUser().getEmail() : null)
                .build();
    }

}


