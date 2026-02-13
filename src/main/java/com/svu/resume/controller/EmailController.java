package com.svu.resume.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.svu.resume.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/email")
@Slf4j
public class EmailController {

    @jakarta.annotation.PostConstruct
    public void init() {
        log.info("EmailController initialized");
    }

    private final EmailService emailService;
    private final com.svu.resume.service.FileUploadService fileUploadService;

    @PostMapping(value = "/send-email", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> sendResumeByEmail(
            @RequestParam("recipientEmail") String recipientEmail,
            @RequestParam("subject") String subject, // Kept for compatibility, but might be overridden or unused if we
                                                     // use fixed subject
            @RequestParam("message") String message, // Kept for compatibility
            @RequestParam("pdfFile") MultipartFile pdfFile,
            Authentication authentication) {

        log.info("Received email request for: {}", recipientEmail);

        if (pdfFile == null || pdfFile.isEmpty()) {
            throw new RuntimeException("pdf file is required");
        }

        if (Objects.isNull(recipientEmail)) {
            throw new RuntimeException("email is required");
        }

        try {
            // 1. Upload PDF to Cloudinary
            log.info("Uploading PDF to Cloudinary...");
            com.svu.resume.service.FileUploadService fileUploadService = this.fileUploadService; // Need to inject this
            String downloadUrl = fileUploadService.uploadResumePdf(pdfFile);
            log.info("PDF uploaded successfully. Download URL: {}", downloadUrl);

            // 2. Send Email with Link
            log.info("Sending email to: {}", recipientEmail);
            emailService.sendDownloadableResume(recipientEmail, downloadUrl);
            log.info("Email sent successfully.");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Resume sent successfully to " + recipientEmail);
            response.put("downloadUrl", downloadUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to process request: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

}
