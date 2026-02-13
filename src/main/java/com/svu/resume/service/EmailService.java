package com.svu.resume.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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

  @Value("${mail.sender}")
  private String senderEmail; // verified brevo email

  private final RestTemplate restTemplate;

  // ===============================
  // VERIFICATION EMAIL
  // ===============================
  public void sendHTMLMail(String to, String subject, String htmlContent) {
    log.info("Sending verification email to {}", to);
    sendEmail(to, subject, htmlContent, null, null);
  }

  // ===============================
  // RESUME WITH PDF ATTACHMENT
  // ===============================
  public void sendEmailWithAttachment(String to,
      String subject,
      String body,
      byte[] attachmentBytes,
      String filename) {

    log.info("Sending resume PDF email to {}", to);

    String base64File = null;
    if (attachmentBytes != null) {
      base64File = Base64.getEncoder().encodeToString(attachmentBytes);
    }

    sendEmail(to, subject, body, base64File, filename);
  }

  // ===============================
  // CORE BREVO METHOD
  // ===============================
  private void sendEmail(String to,
      String subject,
      String htmlContent,
      String base64Attachment,
      String filename) {

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("api-key", mailApiKey);

      Map<String, Object> bodyMap = new java.util.HashMap<>();
      bodyMap.put("sender", java.util.Map.of("name", "Resume Builder", "email", senderEmail));
      bodyMap.put("to", java.util.List.of(java.util.Map.of("email", to)));
      bodyMap.put("subject", subject);
      bodyMap.put("htmlContent", htmlContent);

      if (base64Attachment != null && filename != null) {
        bodyMap.put("attachment", java.util.List.of(
            java.util.Map.of("content", base64Attachment, "name", filename)));
      }

      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(bodyMap, headers);

      ResponseEntity<String> response = restTemplate.postForEntity(mailApiUrl, entity, String.class);

      log.info("BREVO RESPONSE: {}", response.getBody());
      log.info("EMAIL SENT SUCCESSFULLY to {}", to);

    } catch (Exception e) {
      log.error("EMAIL FAILED: {}", e.getMessage());
      throw new RuntimeException("Email sending failed", e);
    }
  }
}
