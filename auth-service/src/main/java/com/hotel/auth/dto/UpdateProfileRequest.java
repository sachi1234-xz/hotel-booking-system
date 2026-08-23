package com.hotel.auth.dto;

import jakarta.validation.constraints.Email;

public class UpdateProfileRequest {

    @Email(message = "Email must be valid")
    private String email;

    private String role;

    public UpdateProfileRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
