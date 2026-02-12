package com.svu.resume.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message="name is required")
    @Size(min=2,max=15,message="name must be between 2 and 15 characters")
    private String name;
    @Email(message="email must be valid")
    @NotBlank(message="email is required")
    private String email;
    @Size(min=8,max=20,message="password must be between 8 and 20 characters")
    @NotBlank(message="password is required")
    private String password;
    private String profileImageUrl;

}
