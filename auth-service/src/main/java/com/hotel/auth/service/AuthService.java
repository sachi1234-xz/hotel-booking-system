package com.hotel.auth.service;

import com.hotel.auth.dto.AuthRequest;
import com.hotel.auth.dto.AuthResponse;
import com.hotel.auth.entity.User;
import com.hotel.auth.exception.ApiException;
import com.hotel.auth.repository.UserRepository;
import com.hotel.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String DEFAULT_ROLE = "CUSTOMER";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(AuthRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(DEFAULT_ROLE)
                .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    public AuthResponse login(AuthRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Password is required");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
                .token(jwtUtil.generateToken(user))
                .username(user.getUsername())
                .role(user.getRole())
                .expiresAt(System.currentTimeMillis() + jwtUtil.getExpiration())
                .build();
    }
}
