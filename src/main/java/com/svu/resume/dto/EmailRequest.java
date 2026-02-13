package com.svu.resume.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    private String email; // renamed from 'to' or alias
    private String downloadUrl;
    private String subject;
    private String message; // renamed from 'body'
}
