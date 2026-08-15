package com.hotel.auth.service;

import com.hotel.auth.dto.AuthRequest;
import com.hotel.auth.dto.AuthResponse;
import com.hotel.auth.entity.User;
import com.hotel.auth.exception.ApiException;
import com.hotel.auth.repository.UserRepository;
import com.hotel.auth.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String SECRET = "mySuperSecretKeyThatShouldBeAtLeast32CharactersLong";

    @Mock
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil = new JwtUtil(SECRET, 86400000L);

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void register_shouldCreateUserAndReturnToken() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        AuthResponse response = authService.register(
                AuthRequest.builder().username("alice").password("secret123").email("alice@example.com").build());

        assertNotNull(response.getToken());
        assertEquals("alice", response.getUsername());
        assertEquals("CUSTOMER", response.getRole());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertNotEquals("secret123", saved.getPassword());
        assertTrue(passwordEncoder.matches("secret123", saved.getPassword()));
    }

    @Test
    void register_shouldRejectDuplicateUsername() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class, () ->
                authService.register(AuthRequest.builder().username("alice").password("secret123").build()));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void login_shouldReturnTokenForValidCredentials() {
        User user = User.builder()
                .id(1L)
                .username("alice")
                .email("alice@example.com")
                .password(passwordEncoder.encode("secret123"))
                .role("CUSTOMER")
                .build();
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(
                AuthRequest.builder().username("alice").password("secret123").build());

        assertNotNull(response.getToken());
        assertEquals("alice", response.getUsername());
    }

    @Test
    void login_shouldRejectWrongPassword() {
        User user = User.builder()
                .id(1L)
                .username("alice")
                .password(passwordEncoder.encode("correct"))
                .role("CUSTOMER")
                .build();
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        ApiException ex = assertThrows(ApiException.class, () ->
                authService.login(AuthRequest.builder().username("alice").password("wrong").build()));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }

    @Test
    void login_shouldRejectUnknownUser() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () ->
                authService.login(AuthRequest.builder().username("ghost").password("whatever").build()));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }
}
