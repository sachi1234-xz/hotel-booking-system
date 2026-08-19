package com.hotel.auth.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.auth.dto.AuthResponse;
import com.hotel.auth.dto.LoginRequest;
import com.hotel.auth.dto.RegisterRequest;
import com.hotel.auth.service.AuthService;
import com.hotel.auth.util.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// REST controller exposing register, login and token validation endpoints.
@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "User registration, login and JWT validation")
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
        String token = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
        }

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
}
