package com.svu.resume.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoginRequest {
    
    @NotBlank(message="email is required")
    @Email(message="enter a valid email")
    private String email;
    @NotBlank(message="password is required")
    private String password;
}
