package com.hotel.auth.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hotel.auth.dto.AuthResponse;
import com.hotel.auth.dto.ChangePasswordRequest;
import com.hotel.auth.dto.LoginRequest;
import com.hotel.auth.dto.PasswordResetRequest;
import com.hotel.auth.dto.RegisterRequest;
import com.hotel.auth.dto.UpdateProfileRequest;
import com.hotel.auth.dto.UserResponse;
import com.hotel.auth.dto.VerifyEmailRequest;
import com.hotel.auth.entity.User;
import com.hotel.auth.exception.AuthException;
import com.hotel.auth.repository.UserRepository;
import com.hotel.auth.util.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final Map<String, String> passwordResetTokens = new HashMap<>();
    private final Map<String, String> emailVerificationCodes = new HashMap<>();

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthException("Email is already registered", HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);

        String token = jwtUtil.generateToken(saved.getEmail(), saved.getRole());
        return new AuthResponse(token, saved.getEmail(), saved.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getEmail(), user.getRole());
    }

    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found", HttpStatus.NOT_FOUND));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!request.getEmail().equals(user.getEmail())) {
                if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                    throw new AuthException("Email is already in use", HttpStatus.CONFLICT);
                }
                user.setEmail(request.getEmail());
            }
        }

        if (request.getRole() != null && !request.getRole().isBlank()) {
            user.setRole(request.getRole());
        }

        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AuthException("Current password is incorrect", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void deleteAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found", HttpStatus.NOT_FOUND));
        userRepository.delete(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found", HttpStatus.NOT_FOUND));
        return toUserResponse(user);
    }

    public Map<String, String> requestPasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("User not found", HttpStatus.NOT_FOUND));

        String code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        passwordResetTokens.put(request.getEmail(), code);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset code sent to your email");
        response.put("email", request.getEmail());
        response.put("code", code);
        return response;
    }

    public void verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("User not found", HttpStatus.NOT_FOUND));

        if (user.isEmailVerified()) {
            throw new AuthException("Email is already verified", HttpStatus.BAD_REQUEST);
        }

        String storedCode = emailVerificationCodes.get(request.getEmail());
        if (storedCode == null || !storedCode.equals(request.getVerificationCode())) {
            throw new AuthException("Invalid verification code", HttpStatus.BAD_REQUEST);
        }

        user.setEmailVerified(true);
        userRepository.save(user);
        emailVerificationCodes.remove(request.getEmail());
    }

    public Map<String, String> sendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found", HttpStatus.NOT_FOUND));

        if (user.isEmailVerified()) {
            throw new AuthException("Email is already verified", HttpStatus.BAD_REQUEST);
        }

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        emailVerificationCodes.put(email, code);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Verification code sent to your email");
        response.put("email", email);
        response.put("code", code);
        return response;
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.isEmailVerified(),
                user.getCreatedAt()
        );
    }
}
