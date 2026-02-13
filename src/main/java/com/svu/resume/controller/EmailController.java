package com.svu.resume.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.svu.resume.dto.EmailRequest;
import com.svu.resume.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/email")
@Slf4j
public class EmailController {

    private final EmailService emailService;

    @jakarta.annotation.PostConstruct
    public void init() {
        log.info("EmailController initialized");
    }

    @PostMapping("/send-email")
    public ResponseEntity<Map<String, Object>> sendResumeByEmail(@RequestBody EmailRequest request,
            Authentication authentication) {
        log.info("Received email request for: {}", request.getEmail());
        log.info("Download URL received: {}", request.getDownloadUrl());

        // 1. Validation
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            log.warn("Email is missing in request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Email is required"));
        }
        if (request.getDownloadUrl() == null || request.getDownloadUrl().isEmpty()) {
            log.warn("Download URL is missing in request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Download URL is required"));
        }

        try {
            // 2. Convert URL to Force Download
            String downloadUrl = convertToDownloadUrl(request.getDownloadUrl());
            log.info("Converted Download URL: {}", downloadUrl);

            // 3. Send Email
            emailService.sendDownloadableResume(request.getEmail(), downloadUrl);
            log.info("Email sent successfully to: {}", request.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Resume sent successfully!");
            response.put("downloadUrl", downloadUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to process request: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
    private String convertToDownloadUrl(String url) {

    if (url == null || url.isBlank()) {
        return url;
    }

    try {
        // already converted
        if (url.contains("fl_attachment")) {
            return url;
        }

        int index = url.indexOf("/upload/");
        if (index == -1) {
            return url;
        }

        String before = url.substring(0, index + 8); // includes /upload/
        String after = url.substring(index + 8);     // keep version + folder + file

        String finalUrl = before + "fl_attachment/" + after;

        log.info("Converted Download URL: {}", finalUrl);
        return finalUrl;

    } catch (Exception e) {
        log.error("Error converting Cloudinary URL", e);
        return url;
    }
}
}
