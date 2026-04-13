package com.camel.clinic.service.auth;

import com.camel.clinic.dto.auth.*;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> login(LoginRequestDTO req) throws BadRequestException;

    ResponseEntity<?> register(RegisterRequestDTO req) throws BadRequestException;

    ResponseEntity<?> refresh(RefreshRequestDTO req) throws BadRequestException;

    ResponseEntity<?> logout(String refreshToken, String accessToken) throws BadRequestException;

    ResponseEntity<?> forgotPassword(ForgotPasswordRequestDTO req) throws BadRequestException;

    ResponseEntity<?> verifyOtp(VerifyOtpRequestDTO req) throws BadRequestException;

    ResponseEntity<?> resetPassword(ResetPasswordRequestDTO req) throws BadRequestException;

    ResponseEntity<?> me(String email) throws BadRequestException;

    ResponseEntity<?> changePassword(ChangePasswordRequestDTO req, String email) throws BadRequestException;

}

