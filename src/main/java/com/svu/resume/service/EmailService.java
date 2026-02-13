package com.svu.resume.service;

import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
      log.info("Encoding attachment of size: {} bytes", attachmentBytes.length);
      base64File = Base64.getEncoder().encodeToString(attachmentBytes);
      log.info("Attachment encoded successfully. Base64 length: {}", base64File.length());
    } else {
      log.warn("Attachment bytes are null");
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

  // ===============================
  // RESUME WITH DOWNLOAD LINK
  // ===============================
  public void sendDownloadableResume(String to, String downloadLink) {
    log.info("Sending downloadable resume email to {}", to);
    String subject = "Your Resume is Ready";

    String htmlContent = String.format(
        """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; border-radius: 8px; }
                    .header { text-align: center; padding-bottom: 20px; border-bottom: 1px solid #ddd; }
                    .content { padding: 20px 0; text-align: center; }
                    .button { display: inline-block; padding: 12px 24px; background-color: #007bff; color: white !important; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px; margin: 20px 0; }
                    .footer { text-align: center; font-size: 12px; color: #777; margin-top: 20px; border-top: 1px solid #ddd; padding-top: 20px; }
                    .link-text { word-break: break-all; color: #007bff; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>Your Resume is Ready!</h2>
                    </div>
                    <div class="content">
                        <p>Hello,</p>
                        <p>Your professional resume has been successfully generated.</p>
                        <p>Click the button below to download your PDF resume instantly:</p>

                        <a href="%s" class="button">Download Resume PDF</a>

                        <p>If the button doesn't work, copy and paste this link into your browser:</p>
                        <p><a href="%s" class="link-text">%s</a></p>
                    </div>
                    <div class="footer">
                        <p>Thank you for using our Resume Builder.</p>
                        <p>&copy; 2024 Resume Builder. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
        downloadLink, downloadLink, downloadLink);

    sendEmail(to, subject, htmlContent, null, null);
  }
}
