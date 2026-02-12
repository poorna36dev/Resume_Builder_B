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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.svu.resume.service.EmailService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/email")
@Slf4j
public class EmailController {
    
    private final EmailService emailService;
    @PostMapping(value="/send-email",consumes=MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Object>> sendResumeByEmail(
        @RequestPart("recipientEmail") String recipientEmail,
        @RequestPart("subject") String subject,
        @RequestPart("message") String message,
        @RequestPart("pdfFile") MultipartFile pdfFile,
        Authentication authentication) throws IOException, MessagingException{

        if(Objects.isNull(recipientEmail) || Objects.isNull(pdfFile)){
            throw new RuntimeException("email and pdf is required");
        }
        byte[] pdfBytes=pdfFile.getBytes();
        String originalFilename=pdfFile.getOriginalFilename();
        String filename=Objects.nonNull(originalFilename)?originalFilename:"resume.pdf";
        String emailSubject=Objects.nonNull(subject)?subject:"Resume Application";
        String emailBody=Objects.nonNull(message)?message:"find the attached Resume below\n\n BestRegards";
        emailService.sendEmailWithAttachment(recipientEmail,emailSubject,emailBody,pdfBytes,filename);
        Map<String,Object> response=new HashMap<>();
        response.put("success",true);
        response.put("message","resume sent successfully"+recipientEmail);
        return ResponseEntity.ok(response);
    }

} 

