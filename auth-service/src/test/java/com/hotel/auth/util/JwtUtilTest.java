package com.hotel.auth.util;

import com.hotel.auth.entity.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("mySuperSecretKeyThatShouldBeAtLeast32CharactersLong", 86400000L);
    }

    @Test
    void generateToken_shouldContainUserClaims() {
        User user = User.builder()
                .id(1L)
                .username("alice")
                .email("alice@example.com")
                .password("encoded")
                .role("CUSTOMER")
                .build();

        String token = jwtUtil.generateToken(user);
        Claims claims = jwtUtil.parseToken(token);

        assertEquals("1", claims.getSubject());
        assertEquals("CUSTOMER", claims.get("role", String.class));
        assertEquals("alice", claims.get("username", String.class));
        assertNotNull(claims.getIssuedAt());
    }

    @Test
    void generateToken_shouldExpireAfterConfiguredDuration() {
        User user = User.builder().id(2L).username("bob").email("bob@example.com")
                .password("encoded").role("ADMIN").build();

        String token = jwtUtil.generateToken(user);
        Claims claims = jwtUtil.parseToken(token);

        Date now = new Date();
        Date expectedExpiry = new Date(now.getTime() + 86400000L);
        assertTrue(Math.abs(claims.getExpiration().getTime() - expectedExpiry.getTime()) < 5000L);
    }
}
