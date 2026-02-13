package com.svu.resume.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.web.bind.annotation.RequestParam;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
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

    private final EmailService emailService;

    @PostMapping(value = "/send-email", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> sendResumeByEmail(
            @RequestParam("recipientEmail") String recipientEmail,
            @RequestParam("subject") String subject,
            @RequestParam("message") String message,
            @RequestParam("pdfFile") MultipartFile pdfFile,
            Authentication authentication) throws IOException {

        log.info("Received email request for: {}", recipientEmail);
        
        if (pdfFile == null || pdfFile.isEmpty()) {
            log.error("PDF File is missing or empty");
            throw new RuntimeException("pdf file is required");
        }
        
        log.info("PDF Size: {} bytes, Original Filename: {}", pdfFile.getSize(), pdfFile.getOriginalFilename());

        if (Objects.isNull(recipientEmail)) {
            throw new RuntimeException("email is required");
        }
        byte[] pdfBytes = pdfFile.getBytes();
        String originalFilename = pdfFile.getOriginalFilename();
        String filename = Objects.nonNull(originalFilename) ? originalFilename : "resume.pdf";
        String emailSubject = (subject == null || subject.trim().isEmpty())
        ? "Resume Application"
        : subject;

        String emailBody = (message == null || message.trim().isEmpty())
        ? "Please find attached my resume.\n\nBest regards"
        : message;

        emailService.sendEmailWithAttachment(recipientEmail, emailSubject, emailBody, pdfBytes, filename);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "resume sent successfully" + recipientEmail);
        return ResponseEntity.ok(response);
    }

}
