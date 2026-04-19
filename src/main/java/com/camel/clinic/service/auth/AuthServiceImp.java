package com.camel.clinic.service.auth;

import com.camel.clinic.dto.auth.*;
import com.camel.clinic.entity.*;
import com.camel.clinic.repository.DoctorRepository;
import com.camel.clinic.repository.PatientRepository;
import com.camel.clinic.repository.SpecialtyRepository;
import com.camel.clinic.repository.StaffRepository;
import com.camel.clinic.service.EmailUniqueService;
import com.camel.clinic.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class AuthServiceImp implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final AuthServiceInv authServiceInv;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final TokenStoreService tokenStoreService;
    private final OtpService otpService;
    private final EmailService emailService;
    private final EmailUniqueService emailUniqueService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final SpecialtyRepository specialtyRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> login(LoginRequestDTO request) throws BadRequestException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = authServiceInv.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        // Check if user is active
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new BadRequestException(
                    "Account is not active");
        }

        // Update last login
        user.setLastLogin(new Date());
        authServiceInv.save(user);

        // Generate tokens with full user information
        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // Store refresh token hash (for refresh/logout)
        tokenStoreService.storeRefreshTokenHash(user.getId().toString(), refreshToken);

        log.info("User logged in successfully with email: {}", request.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("user", UserResponseDTO.from(user));
        response.put("refreshToken", refreshToken);
        response.put("accessToken", accessToken);

        return ResponseEntity.ok(response);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> register(RegisterRequestDTO req) throws BadRequestException {
        String email = req.getEmail().trim().toLowerCase();
        Role.RoleName role = Optional.ofNullable(req.getRole()).orElse(Role.RoleName.PATIENT);

        if (emailUniqueService.existsInCache(email)) {
            throw new BadRequestException("Email already exists");
        }

        if (authServiceInv.findByEmail(email).isPresent()) {
            emailUniqueService.addToCache(email);
            throw new BadRequestException("Email already exists");
        }

        if (authServiceInv.findByPhone(req.getPhone()).isPresent()) {
            throw new BadRequestException("Phone number already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setFullName(req.getName());
        user.setPhone(req.getPhone());
        user.setDateOfBirth(req.getDateOfBirth());
        user.setGender(req.getGender());
        user.setRole(role);
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        user = authServiceInv.save(user);
        createRoleProfile(user, req, role);

        emailUniqueService.addToCache(email);

        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        tokenStoreService.storeRefreshTokenHash(user.getId().toString(), refreshToken);

//        TODO: Send welcome email asynchronously bằng rabitMQ
        Map<String, Object> response = new HashMap<>();
        response.put("user", UserResponseDTO.from(user));
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        return ResponseEntity.ok(response);
    }

    private void createRoleProfile(User user, RegisterRequestDTO req, Role.RoleName role) throws BadRequestException {
        switch (role) {
            case PATIENT -> createPatientProfile(user, req);
            case STAFF -> createStaffProfile(user);
            case DOCTOR -> createDoctorProfile(user, req);
            default -> throw new BadRequestException("Unsupported role for self-registration");
        }
    }

    private void createPatientProfile(User user, RegisterRequestDTO req) {
        Patient patient = new Patient();
        patient.setUser(user);
        patient.setPatientCode(generateUniqueCode("PT", patientRepository::existsByPatientCode));
        patient.setDateOfBirth(java.sql.Date.valueOf(LocalDate.parse(req.getDateOfBirth())));
        patient.setGender(mapPatientGender(req.getGender()));
        patientRepository.save(patient);
    }

    private void createStaffProfile(User user) {
        Staff staff = new Staff();
        staff.setUser(user);
        staff.setStaffCode(generateUniqueCode("ST", staffRepository::existsByStaffCode));
        staff.setHireDate(new Date());
        staffRepository.save(staff);
    }

    private void createDoctorProfile(User user, RegisterRequestDTO req) throws BadRequestException {
        UUID specialtyId = req.getSpecialtyId();
        if (specialtyId == null) {
            throw new BadRequestException("specialtyId is required for doctor registration");
        }

        Specialty specialty = specialtyRepository.findById(specialtyId)
                .filter(Specialty::getIsActive)
                .orElseThrow(() -> new BadRequestException("Specialty not found or inactive"));

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setDoctorCode(generateUniqueCode("DR", doctorRepository::existsByDoctorCode));
        doctor.setSpecialty(specialty);
        doctorRepository.save(doctor);
    }

    private Patient.Gender mapPatientGender(User.Gender gender) {
        return switch (gender) {
            case MALE -> Patient.Gender.male;
            case FEMALE -> Patient.Gender.female;
            default -> Patient.Gender.other;
        };
    }

    private String generateUniqueCode(String prefix, Predicate<String> existsCheck) {
        String code;
        do {
            code = prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (existsCheck.test(code));
        return code;
    }

    @Override
    public ResponseEntity<?> refresh(RefreshRequestDTO req) throws BadRequestException {
        String refreshToken = req.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException("refreshToken is required");
        }
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }

        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        if (!tokenStoreService.matchesRefreshToken(userId, refreshToken)) {
            throw new BadRequestException("Refresh token revoked");
        }
        User user = authServiceInv.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new BadRequestException("User not found"));

        String newAccessToken = jwtUtil.generateToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());
        tokenStoreService.storeRefreshTokenHash(user.getId().toString(), newRefreshToken);

        Map<String, Object> response = new HashMap<>();
        response.put("user", UserResponseDTO.from(user));
        response.put("accessToken", newAccessToken);
        response.put("refreshToken", newRefreshToken);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> logout(String refreshToken, String accessToken) throws BadRequestException {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException("refreshToken is required");
        }
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        tokenStoreService.deleteRefreshToken(userId);

        if (accessToken != null && jwtUtil.validateToken(accessToken) && jwtUtil.isAccessToken(accessToken)) {
            String jti = jwtUtil.getJtiFromToken(accessToken);
            if (jti != null) {
                Integer expSeconds = jwtUtil.getExpirationSecondsFromToken(accessToken);

                tokenStoreService.blacklistJti(jti, Duration.ofSeconds(expSeconds));
            }
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> forgotPassword(ForgotPasswordRequestDTO req) throws BadRequestException {
        String email = Optional.ofNullable(req.getEmail()).orElse("").trim().toLowerCase();
        // Do not leak existence, but for simplicity we still validate format via DTO.
        authServiceInv.findByEmail(email).ifPresent(u -> {
            String otp = otpService.generateAndStoreOtp(email);
            emailService.sendOtpEmail(email, otp);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("message", "If the email exists, an OTP has been sent");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> verifyOtp(VerifyOtpRequestDTO req) throws BadRequestException {
        String resetToken = otpService.verifyOtpAndIssueResetToken(req.getEmail(), req.getOtp());
        if (resetToken == null) {
            throw new BadRequestException("Invalid OTP");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("resetToken", resetToken);
        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> resetPassword(ResetPasswordRequestDTO req) throws BadRequestException {
        String email = otpService.consumeResetToken(req.getResetToken());
        if (email == null) {
            throw new BadRequestException("Invalid reset token");
        }
        User user = authServiceInv.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        authServiceInv.save(user);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> me(String email) throws BadRequestException {
        User user = authServiceInv.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        Map<String, Object> response = new HashMap<>();
        response.put("user", UserResponseDTO.from(user));
        response.put("profile", null);
        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> changePassword(ChangePasswordRequestDTO req, String email) throws BadRequestException {
        User user = authServiceInv.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        authServiceInv.save(user);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> getUserProfile(String email) throws BadRequestException {
        User user = authServiceInv.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        UserProfileDTO profile = authServiceInv.buildUserProfileDTO(user);
        return ResponseEntity.ok(profile);
    }
}
