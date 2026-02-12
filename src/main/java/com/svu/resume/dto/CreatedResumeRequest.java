package com.svu.resume.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatedResumeRequest {
    
    @NotBlank(message="title is required")
    private String title;
}
