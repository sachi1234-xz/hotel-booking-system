package com.hotel.auth.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.auth.dto.AuthResponse;
import com.hotel.auth.dto.ChangePasswordRequest;
import com.hotel.auth.dto.LoginRequest;
import com.hotel.auth.dto.PasswordResetRequest;
import com.hotel.auth.dto.RegisterRequest;
import com.hotel.auth.dto.UpdateProfileRequest;
import com.hotel.auth.dto.UserResponse;
import com.hotel.auth.dto.VerifyEmailRequest;
import com.hotel.auth.service.AuthService;
import com.hotel.auth.util.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "User registration, login, profile management and JWT validation")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates the user, hashes the password and returns a JWT")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Validates credentials and returns a JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate a JWT", description = "Checks the Bearer token and returns validity plus its claims")
    public ResponseEntity<Map<String, Object>> validate(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);

        if (token == null || !jwtUtil.validateToken(token)) {
            Map<String, Object> invalid = new LinkedHashMap<>();
            invalid.put("valid", false);
            invalid.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(invalid);
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("valid", true);
        body.put("email", jwtUtil.getEmail(token));
        body.put("role", jwtUtil.getRole(token));
        return ResponseEntity.ok(body);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update profile", description = "Update email or role for the authenticated user")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @Valid @RequestBody UpdateProfileRequest request) {
        String email = userEmail != null ? userEmail : extractEmailFromAuth(null);
        UserResponse response = authService.updateProfile(email, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password after verifying current password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @Valid @RequestBody ChangePasswordRequest request) {
        String email = userEmail != null ? userEmail : extractEmailFromAuth(null);
        authService.changePassword(email, request);
        Map<String, String> body = new LinkedHashMap<>();
        body.put("message", "Password changed successfully");
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/profile")
    @Operation(summary = "Delete account", description = "Delete the authenticated user's account")
    public ResponseEntity<Map<String, String>> deleteAccount(
            @RequestHeader(value = "X-User-Email", required = false) String userEmail) {
        String email = userEmail != null ? userEmail : extractEmailFromAuth(null);
        authService.deleteAccount(email);
        Map<String, String> body = new LinkedHashMap<>();
        body.put("message", "Account deleted successfully");
        return ResponseEntity.ok(body);
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Admin endpoint to list all registered users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID", description = "Admin endpoint to get a specific user")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    @PostMapping("/password-reset/request")
    @Operation(summary = "Request password reset", description = "Generates a password reset code for the given email")
    public ResponseEntity<Map<String, String>> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequest request) {
        Map<String, String> response = authService.requestPasswordReset(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verifies email with the provided verification code")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request);
        Map<String, String> body = new LinkedHashMap<>();
        body.put("message", "Email verified successfully");
        return ResponseEntity.ok(body);
    }

    @PostMapping("/verification-code/send")
    @Operation(summary = "Send verification code", description = "Generates and returns a verification code for email verification")
    public ResponseEntity<Map<String, String>> sendVerificationCode(@RequestParam String email) {
        Map<String, String> response = authService.sendVerificationCode(email);
        return ResponseEntity.ok(response);
    }

    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }

    private String extractEmailFromAuth(String authorization) {
        String token = extractToken(authorization);
        if (token != null && jwtUtil.validateToken(token)) {
            return jwtUtil.getEmail(token);
        }
        throw new com.hotel.auth.exception.AuthException("Authentication required", HttpStatus.UNAUTHORIZED);
    }
}
