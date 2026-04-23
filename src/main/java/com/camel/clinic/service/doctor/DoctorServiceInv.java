package com.camel.clinic.service.doctor;

import com.camel.clinic.dto.doctor.*;
import com.camel.clinic.entity.*;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.exception.NotFoundException;
import com.camel.clinic.exception.UnauthorizedException;
import com.camel.clinic.repository.*;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DoctorServiceInv extends BaseService<Doctor, DoctorRepository> {
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorLeaveRepository doctorLeaveRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;

    public DoctorServiceInv(DoctorRepository repository, DoctorScheduleRepository doctorScheduleRepository,
                            DoctorLeaveRepository doctorLeaveRepository, UserRepository userRepository, DoctorRepository doctorRepository,
                            AppointmentRepository appointmentRepository, SpecialtyRepository specialtyRepository) {
        super(Doctor::new, repository);
        this.doctorScheduleRepository = doctorScheduleRepository;
        this.doctorLeaveRepository = doctorLeaveRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.specialtyRepository = specialtyRepository;
    }

    private static DoctorInfoDto getDoctorInfoDto(DoctorSchedule todaySchedule, Doctor doctorInfo, Specialty specialty, Date nextSlot, String workplace) {
        Boolean availableToday = todaySchedule != null && todaySchedule.getIsActive();

        DoctorInfoDto doctorInfoDto = new DoctorInfoDto(
                doctorInfo.getId().toString(),
                doctorInfo.getDegree(),
                doctorInfo.getExperienceYears(),
                doctorInfo.getTotalReviews(),
                doctorInfo.getEducation(),
                doctorInfo.getAverageRating(),
                doctorInfo.getUser().getFullName(),
                doctorInfo.getUser().getPathAvatar(),
                doctorInfo.getConsultationFee(),
                doctorInfo.getBio(),
                availableToday, // Placeholder for availableToday
                nextSlot,  // Placeholder for nextSlot
                workplace,  // Placeholder for workplace
                specialty
        );
        return doctorInfoDto;
    }

    public ResponseEntity<?> getTopDoctors() {
        List<DoctorDTO> doctors = repository.getTopDoctors();

        return ResponseEntity.ok(doctors);
    }

    public ResponseEntity<?> filterDoctors(Map<String, Object> queryParams) {
        try {
            int page = parseIntParam(queryParams, "page", 0);
            int size = parseIntParam(queryParams, "size", 20);
            String doctorName = getStringParam(queryParams, "doctorName", "name", "q");
            String specialtyName = getStringParam(queryParams, "specialtyName", "specialty");
            String specialtyIdStr = getStringParam(queryParams, "specialtyId");
            UUID specialtyId = null;
            if (specialtyIdStr != null) {
                try {
                    specialtyId = UUID.fromString(specialtyIdStr);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid specialtyId format: {}", specialtyIdStr, e);
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid specialtyId format",
                            "message", "specialtyId must be a valid UUID"));
                }
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<DoctorDTO> resultPage = repository.filterDoctors(doctorName, specialtyName, specialtyId, pageable);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("data", resultPage.getContent());
            response.put("page", resultPage.getNumber());
            response.put("size", resultPage.getSize());
            response.put("totalItems", resultPage.getTotalElements());
            response.put("totalPages", resultPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error filtering doctors: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to filter doctors", "message", e.getMessage()));
        }
    }

    // Doctor Schedule Management
    public ResponseEntity<?> getDoctorSchedules(String doctorId) {
        try {
            UUID doctorUUID = UUID.fromString(doctorId);
            Doctor doctor = doctorRepository.findByUserId(doctorUUID)
                    .orElseThrow(() -> new NotFoundException("Doctor not found"));

            List<DoctorSchedule> schedules = doctorScheduleRepository.findByDoctorId(doctor.getId());
            List<DoctorScheduleResponseDTO> response = schedules.stream()
                    .map(this::convertToScheduleDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting doctor schedules: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to get schedules", "message", e.getMessage()));
        }
    }

    public ResponseEntity<?> addDoctorSchedules(List<DoctorScheduleRequestDTO> requestDTOs) {
        try {
            User currentUser = getCurrentUser();
            Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NotFoundException("Doctor not found"));

            // Check duplicate trong chính request
            List<Integer> incomingDays = requestDTOs.stream()
                    .map(DoctorScheduleRequestDTO::getDayOfWeek)
                    .toList();

            Set<Integer> seen = new HashSet<>();
            for (Integer day : incomingDays) {
                if (!seen.add(day)) {
                    throw new BadRequestException("Trùng ngày trong request: " + day);
                }
            }

            // Check đã tồn tại trong DB
            List<Integer> existingDays = doctorScheduleRepository
                    .findByDoctorIdAndDayOfWeekIn(doctor.getId(), incomingDays)
                    .stream()
                    .map(DoctorSchedule::getDayOfWeek)
                    .toList();

            if (!existingDays.isEmpty()) {
                String days = existingDays.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "));
                throw new BadRequestException("Đã có lịch làm việc cho ngày: " + days);
            }

            // Map DTO -> Entity
            List<DoctorSchedule> schedules = requestDTOs.stream().map(dto -> {
                DoctorSchedule s = new DoctorSchedule();
                s.setDoctor(doctor);
                s.setDayOfWeek(dto.getDayOfWeek());
                s.setStartTime(dto.getStartTime());
                s.setEndTime(dto.getEndTime());
                s.setSlotDuration(dto.getSlotDuration());
                s.setMaxPatientsPerSlot(dto.getMaxPatientsPerSlot());
                s.setLocation(dto.getLocation());
                s.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
                return s;
            }).toList();

            // Bulk insert
            List<?> result = doctorScheduleRepository.saveAll(schedules)
                    .stream()
                    .map(this::convertToScheduleDTO)
                    .toList();

            return ResponseEntity.ok(result);

        } catch (NotFoundException | BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error adding doctor schedules: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to add schedules"));
        }
    }

    public ResponseEntity<?> deleteDoctorSchedule(String scheduleId) {
        try {
            UUID scheduleUUID = UUID.fromString(scheduleId);
            DoctorSchedule schedule = doctorScheduleRepository.findById(scheduleUUID)
                    .orElseThrow(() -> new NotFoundException("Schedule not found"));

            User currentUser = getCurrentUser();
            if (!schedule.getDoctor().getUser().getId().equals(currentUser.getId())) {
                throw new UnauthorizedException("You can only delete your own schedules");
            }

            doctorScheduleRepository.delete(schedule);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid schedule ID format", "message", "scheduleId must be a valid UUID"));
        } catch (NotFoundException | UnauthorizedException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting doctor schedule: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to delete schedule", "message", e.getMessage()));
        }
    }

    // Doctor Leave Management
    public ResponseEntity<?> requestDoctorLeave(DoctorLeaveRequestDTO requestDTO) {
        try {
            User currentUser = getCurrentUser();
            Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NotFoundException("Doctor not found"));

            boolean isDuplicate = doctorLeaveRepository
                    .existsByDoctorIdAndLeaveDateAndStartTimeAndEndTimeAndStatusNot(
                            doctor.getId(),
                            requestDTO.getLeaveDate(),
                            requestDTO.getStartTime(),
                            requestDTO.getEndTime(),
                            DoctorLeave.LeaveStatus.rejected
                    );

            if (isDuplicate) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Duplicate leave request", "message", "You already have a leave request for this time period"));
            }

            DoctorLeave leave = new DoctorLeave();
            leave.setDoctor(doctor);
            leave.setLeaveDate(requestDTO.getLeaveDate());
            leave.setStartTime(requestDTO.getStartTime());
            leave.setEndTime(requestDTO.getEndTime());
            leave.setReason(requestDTO.getReason());
            leave.setStatus(DoctorLeave.LeaveStatus.pending);

            DoctorLeave savedLeave = doctorLeaveRepository.save(leave);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedLeave.getId())
                    .toUri();

            return ResponseEntity.created(location)
                    .body(convertToLeaveDTO(savedLeave));

        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error requesting doctor leave: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to request leave", "message", e.getMessage()));
        }
    }

    public ResponseEntity<?> getDoctorLeaves(String doctorId) {
        try {
            User currentUser = getCurrentUser();

            UUID targetDoctorId;
            if (currentUser.getRole() == Role.RoleName.DOCTOR) {
                // Doctors can only view their own leaves
                Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new NotFoundException("Doctor not found"));
                targetDoctorId = doctor.getId();
            } else if (currentUser.getRole() == Role.RoleName.ADMIN) {
                // Admins can view specific doctor's leaves or all pending leaves
                if (doctorId != null && !doctorId.isEmpty()) {
                    targetDoctorId = UUID.fromString(doctorId);
                } else {
                    // Return all pending leaves for admin review
                    List<DoctorLeave> leaves = doctorLeaveRepository.findByStatus(DoctorLeave.LeaveStatus.pending);
                    List<DoctorLeaveResponseDTO> response = leaves.stream()
                            .map(this::convertToLeaveDTO)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(response);
                }
            } else {
                throw new UnauthorizedException("Invalid role");
            }

            List<DoctorLeave> leaves = doctorLeaveRepository.findByDoctorId(targetDoctorId);
            List<DoctorLeaveResponseDTO> response = leaves.stream()
                    .map(this::convertToLeaveDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid doctor ID format", "message", "doctorId must be a valid UUID"));
        } catch (NotFoundException | UnauthorizedException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting doctor leaves: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to get leaves", "message", e.getMessage()));
        }
    }

    public ResponseEntity<?> approveDoctorLeave(String leaveId, DoctorLeaveApproveRequestDTO requestDTO) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser.getRole() != Role.RoleName.ADMIN) {
                throw new UnauthorizedException("Only admins can approve leaves");
            }

            UUID leaveUUID = UUID.fromString(leaveId);
            DoctorLeave leave = doctorLeaveRepository.findById(leaveUUID)
                    .orElseThrow(() -> new NotFoundException("Leave request not found"));

            if (requestDTO.getStatus() == DoctorLeave.LeaveStatus.approved) {
                leave.setStatus(DoctorLeave.LeaveStatus.approved);
            } else if (requestDTO.getStatus() == DoctorLeave.LeaveStatus.rejected) {
                leave.setStatus(DoctorLeave.LeaveStatus.rejected);
            } else {
                throw new BadRequestException("Invalid status. Must be 'approved' or 'rejected'");
            }

            DoctorLeave updatedLeave = doctorLeaveRepository.save(leave);
            return ResponseEntity.ok(convertToLeaveDTO(updatedLeave));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid leave ID format", "message", "leaveId must be a valid UUID"));
        } catch (NotFoundException | UnauthorizedException | BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error approving doctor leave: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to approve leave", "message", e.getMessage()));
        }
    }

    public ResponseEntity<?> getDoctorInfoSchedules() {
        try {
            User currentUser = getCurrentUser();
            Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NotFoundException("Doctor not found"));

            int today = LocalDate.now().getDayOfWeek().getValue();

            DoctorSchedule schedules = doctorScheduleRepository.findTodaySchedulesByDoctorId(doctor.getId(), today)
                    .orElseThrow(() -> new NotFoundException("Doctor schedule not found for today"));
            DoctorScheduleResponseDTO response = convertToScheduleDTO(schedules);

            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting doctor info schedules: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to get schedules", "message", e.getMessage()));
        }
    }

    public ResponseEntity<?> getDoctorInfo(String doctorId) {
        try {
            Doctor doctorInfo = doctorRepository.getReferenceById(UUID.fromString(doctorId));
            Specialty specialties = specialtyRepository.findByDoctorId(doctorInfo.getId());
            DoctorSchedule todaySchedule = doctorScheduleRepository.findTodaySchedulesByDoctorId(doctorInfo.getId(), LocalDate.now().getDayOfWeek().getValue())
                    .orElse(null);

            Date fromDate = java.sql.Date.valueOf(LocalDate.now());
            Date toDate = java.sql.Date.valueOf(LocalDate.now().plusDays(1));

            List<Date> nextSlots = appointmentRepository.findBookedStartTimesBetween(doctorInfo.getId(), fromDate, toDate);
            Date nextSlot = nextSlots.stream()
                    .filter(slot -> slot.after(new Date()))
                    .min(Date::compareTo)
                    .orElse(null);

            String workplace = todaySchedule != null ? todaySchedule.getLocation() : null;

            DoctorInfoDto doctorInfoDto = getDoctorInfoDto(todaySchedule, doctorInfo, specialties, nextSlot, workplace);

            return ResponseEntity.ok(doctorInfoDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//            throw new BadRequestException("Invalid doctor ID format. Must be a valid UUID");
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting doctor info: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to get doctor info", "message", e.getMessage()));
        }
    }

    // Helper methods
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private DoctorScheduleResponseDTO convertToScheduleDTO(DoctorSchedule schedule) {
        return new DoctorScheduleResponseDTO(
                schedule.getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getSlotDuration(),
                schedule.getMaxPatientsPerSlot(),
                schedule.getLocation(),
                schedule.getIsActive()
        );
    }

    private DoctorLeaveResponseDTO convertToLeaveDTO(DoctorLeave leave) {
        return new DoctorLeaveResponseDTO(
                leave.getId(),
                leave.getLeaveDate(),
                leave.getStartTime(),
                leave.getEndTime(),
                leave.getReason(),
                leave.getStatus().toString(),
                leave.getDoctor().getUser().getFullName(),
                leave.getDoctor().getId()
        );
    }

    private String getStringParam(Map<String, Object> queryParams, String... keys) {
        for (String key : keys) {
            Object value = queryParams.get(key);
            if (value instanceof String str && !str.isBlank()) {
                return str.trim();
            }
        }
        return null;
    }
}
