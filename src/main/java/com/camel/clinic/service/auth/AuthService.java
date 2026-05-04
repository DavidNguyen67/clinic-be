package com.camel.clinic.service.auth;

import com.camel.clinic.dto.auth.*;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.exception.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface AuthService {
    ResponseEntity<?> login(LoginRequestDTO req) throws BadRequestException;

    ResponseEntity<?> register(RegisterRequestDTO req) throws BadRequestException;

    ResponseEntity<?> refresh(RefreshRequestDTO req) throws BadRequestException;

    ResponseEntity<?> logout(String refreshToken, String accessToken) throws BadRequestException;

    ResponseEntity<?> forgotPassword(ForgotPasswordRequestDTO req) throws BadRequestException;

    ResponseEntity<?> verifyOtp(VerifyOtpRequestDTO req) throws BadRequestException;

    ResponseEntity<?> resetPassword(ResetPasswordRequestDTO req) throws BadRequestException;

    ResponseEntity<?> getUserProfile(UUID userId) throws NotFoundException;

    ResponseEntity<?> changePassword(ChangePasswordRequestDTO req, String email) throws BadRequestException;
}

