package com.camel.clinic.service.doctor;

import com.camel.clinic.dto.DoctorDTO;
import com.camel.clinic.dto.doctor.*;
import com.camel.clinic.entity.Doctor;
import com.camel.clinic.entity.DoctorLeave;
import com.camel.clinic.entity.DoctorSchedule;
import com.camel.clinic.entity.Role;
import com.camel.clinic.entity.User;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.exception.NotFoundException;
import com.camel.clinic.exception.UnauthorizedException;
import com.camel.clinic.repository.DoctorLeaveRepository;
import com.camel.clinic.repository.DoctorRepository;
import com.camel.clinic.repository.DoctorScheduleRepository;
import com.camel.clinic.repository.UserRepository;
import com.camel.clinic.service.BaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DoctorServiceInv extends BaseService<Doctor, DoctorRepository> {
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorLeaveRepository doctorLeaveRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final ObjectMapper objectMapper;

    public DoctorServiceInv(DoctorRepository repository, DoctorScheduleRepository doctorScheduleRepository,
                            DoctorLeaveRepository doctorLeaveRepository, UserRepository userRepository, DoctorRepository doctorRepository,
                            ObjectMapper objectMapper) {
        super(Doctor::new, repository);
        this.doctorScheduleRepository = doctorScheduleRepository;
        this.doctorLeaveRepository = doctorLeaveRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.objectMapper = objectMapper;
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
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "Invalid specialtyId format",
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to filter doctors", "message", e.getMessage()));
        }
    }

    // Doctor Schedule Management
    public ResponseEntity<?> getDoctorSchedules() {
        try {
            User currentUser = getCurrentUser();
            Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NotFoundException("Doctor not found"));

            List<DoctorSchedule> schedules = doctorScheduleRepository.findByDoctorId(doctor.getId());
            List<DoctorScheduleResponseDTO> response = schedules.stream()
                    .map(this::convertToScheduleDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting doctor schedules: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get schedules", "message", e.getMessage()));
        }
    }

    public ResponseEntity<?> addDoctorSchedule(Map<String, Object> requestBody) {
        try {
            DoctorScheduleRequestDTO requestDTO = objectMapper.convertValue(requestBody, DoctorScheduleRequestDTO.class);

            if (requestDTO.getDayOfWeek() < 0 || requestDTO.getDayOfWeek() > 6) {
                throw new BadRequestException("Day of week must be between 0-6");
            }

            User currentUser = getCurrentUser();
            Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NotFoundException("Doctor not found"));

            DoctorSchedule schedule = new DoctorSchedule();
            schedule.setDoctor(doctor);
            schedule.setDayOfWeek(requestDTO.getDayOfWeek());
            schedule.setStartTime(requestDTO.getStartTime());
            schedule.setEndTime(requestDTO.getEndTime());
            schedule.setSlotDuration(requestDTO.getSlotDuration());
            schedule.setMaxPatientsPerSlot(requestDTO.getMaxPatientsPerSlot());
            schedule.setLocation(requestDTO.getLocation());
            schedule.setIsActive(requestDTO.getIsActive() != null ? requestDTO.getIsActive() : true);

            DoctorSchedule savedSchedule = doctorScheduleRepository.save(schedule);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToScheduleDTO(savedSchedule));
        } catch (NotFoundException | BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error adding doctor schedule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add schedule", "message", e.getMessage()));
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid schedule ID format"));
        } catch (NotFoundException | UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting doctor schedule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete schedule", "message", e.getMessage()));
        }
    }

    // Doctor Leave Management
    public ResponseEntity<?> requestDoctorLeave(Map<String, Object> requestBody) {
        try {
            DoctorLeaveRequestDTO requestDTO = objectMapper.convertValue(requestBody, DoctorLeaveRequestDTO.class);

            User currentUser = getCurrentUser();
            Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NotFoundException("Doctor not found"));

            DoctorLeave leave = new DoctorLeave();
            leave.setDoctor(doctor);
            leave.setLeaveDate(requestDTO.getLeaveDate());
            leave.setStartTime(requestDTO.getStartTime());
            leave.setEndTime(requestDTO.getEndTime());
            leave.setReason(requestDTO.getReason());
            leave.setStatus(DoctorLeave.LeaveStatus.pending);

            DoctorLeave savedLeave = doctorLeaveRepository.save(leave);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToLeaveDTO(savedLeave));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error requesting doctor leave: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid doctor ID format"));
        } catch (NotFoundException | UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting doctor leaves: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get leaves", "message", e.getMessage()));
        }
    }

    public ResponseEntity<?> approveDoctorLeave(String leaveId, Map<String, Object> requestBody) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser.getRole() != Role.RoleName.ADMIN) {
                throw new UnauthorizedException("Only admins can approve leaves");
            }

            DoctorLeaveApproveRequestDTO requestDTO = objectMapper.convertValue(requestBody, DoctorLeaveApproveRequestDTO.class);

            UUID leaveUUID = UUID.fromString(leaveId);
            DoctorLeave leave = doctorLeaveRepository.findById(leaveUUID)
                    .orElseThrow(() -> new NotFoundException("Leave request not found"));

            if ("approved".equalsIgnoreCase(requestDTO.getStatus())) {
                leave.setStatus(DoctorLeave.LeaveStatus.approved);
            } else if ("rejected".equalsIgnoreCase(requestDTO.getStatus())) {
                leave.setStatus(DoctorLeave.LeaveStatus.rejected);
            } else {
                throw new BadRequestException("Invalid status. Must be 'approved' or 'rejected'");
            }

            DoctorLeave updatedLeave = doctorLeaveRepository.save(leave);
            return ResponseEntity.ok(convertToLeaveDTO(updatedLeave));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid leave ID format"));
        } catch (NotFoundException | UnauthorizedException | BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error approving doctor leave: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to approve leave", "message", e.getMessage()));
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
