package com.svu.resume.dto;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
@Data
@AllArgsConstructor
@Builder
public class AuthResponse {
    @JsonProperty("_id")
    private String id;
    private String name;
    private String email;
    private String password;
    private String profileImageUrl;
    @Builder.Default
    private String subscriptionPlan="basic";
    @Builder.Default
    private boolean emailVerified=false;
    private String verificationToken;
    private LocalDateTime verificationExpires;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private String token;

    
    
}
