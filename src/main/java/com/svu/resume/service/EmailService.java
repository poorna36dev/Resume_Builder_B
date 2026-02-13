package com.svu.resume.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.svu.resume.dto.EmailRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${mail.api.url}")
    private String mailApiUrl;

    @Value("${mail.api.key}")
    private String mailApiKey;

    private final RestTemplate restTemplate;

    public void sendHTMLMail(String to, String subject, String htmlContent) {
        log.info("Sending HTML email to: {}", to);
        sendEmail(to, subject, htmlContent, null, null);
    }

    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String filename) {
        log.info("Sending email with attachment to: {}", to);
        String base64Attachment = attachment != null ? Base64.getEncoder().encodeToString(attachment) : null;
        sendEmail(to, subject, body, base64Attachment, filename);
    }

    private void sendEmail(String to, String subject, String body, String attachment, String filename) {
        try {
            EmailRequest request = EmailRequest.builder()
                    .to(to)
                    .subject(subject)
                    .body(body)
                    .attachment(attachment)
                    .filename(filename)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", mailApiKey);

            HttpEntity<EmailRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(mailApiUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent successfully to {}", to);
            } else {
                log.error("Failed to send email. Status code: {}", response.getStatusCode());
                throw new RuntimeException("Failed to send email: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage());
            throw new RuntimeException("Error sending email", e);
        }
    }
}
