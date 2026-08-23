package com.hotel.auth.dto;

import java.time.LocalDateTime;

public class UserResponse {

    private Long id;
    private String email;
    private String role;
    private boolean emailVerified;
    private LocalDateTime createdAt;

    public UserResponse() {}

    public UserResponse(Long id, String email, String role, boolean emailVerified, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.emailVerified = emailVerified;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
