package com.svu.resume.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Document("users")
public class User {
    
    private String id;
    private String name;
    private String email;
    private String password;
    private String profileImageUrl;
    @Builder.Default
    private String subscriptionPlan="basic";
    @Builder.Default
    private boolean emailVerified=false;
    private String verficationToken;
    private LocalDateTime verificationExpires;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;



}
