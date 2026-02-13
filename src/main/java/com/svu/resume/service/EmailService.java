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

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${mail.api.url}")
    private String mailApiUrl;

    @Value("${mail.api.key}")
    private String mailApiKey;

    @Value("${mail.sender}")
    private String senderEmail;

    private final RestTemplate restTemplate;

    // send verification / normal html email
    public void sendHTMLMail(String to, String subject, String htmlContent) {
        log.info("Sending HTML email to: {}", to);
        sendEmail(to, subject, htmlContent);
    }

    private void sendEmail(String to, String subject, String body) {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 🔥 BREVO API KEY HEADER (IMPORTANT)
            headers.set("api-key", mailApiKey);

            // JSON body for brevo
            String json = """
            {
              "sender": {
                "name": "Resume Builder",
                "email": "%s"
              },
              "to": [
                { "email": "%s" }
              ],
              "subject": "%s",
              "htmlContent": "%s"
            }
            """.formatted(senderEmail, to, subject, body);

            HttpEntity<String> entity = new HttpEntity<>(json, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(mailApiUrl, entity, String.class);

            log.info("BREVO RESPONSE: {}", response.getBody());
            log.info("Email sent successfully to {}", to);

        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage());
            throw new RuntimeException("Email sending failed");
        }
    }
}
