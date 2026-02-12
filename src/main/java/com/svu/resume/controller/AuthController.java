package com.svu.resume.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.svu.resume.document.User;
import com.svu.resume.dto.AuthResponse;
import com.svu.resume.dto.LoginRequest;
import com.svu.resume.dto.RegisterRequest;
import com.svu.resume.service.AuthService;
import com.svu.resume.service.EmailService;
import com.svu.resume.service.FileUploadService;
import static com.svu.resume.util.AppConstants.AUTH_CONTROLLER;
import static com.svu.resume.util.AppConstants.REGISTER;
import static com.svu.resume.util.AppConstants.VERIFY_EMAIL;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(AUTH_CONTROLLER)
public class AuthController {
    private final AuthService authService;
    private final FileUploadService fileUploadService;
    private final EmailService emailService;


    @PostMapping(REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){ 
        log.info("in authController-register()");     
        AuthResponse response=authService.register(request);
        log.info("response from service:{}",response);  
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        log.info("in authController-verifyEmail()"); 
        if(token == null){
            return ResponseEntity.badRequest()
            .body(Map.of("message", "Token is missing"));
        }
        String res=authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message",res));  
    }
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestPart MultipartFile image) throws IOException{
        Map<String,String>result=fileUploadService.uploadSingleImage(image);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) throws Exception{
    try {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    } 
    catch (RuntimeException ex) {
        log.error("Login error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }
}

    @PostMapping("/resend-verfication")
    public ResponseEntity<?> resendVerfication(@RequestBody Map<String,String> map){
        String email=map.get("email");
        if(Objects.isNull(email)){
            return ResponseEntity.badRequest().body(Map.of("message","provide a valid email"));
        }
        authService.resendVerification(email);
        return ResponseEntity.ok(Map.of("success",true,"message","verfication email sent successfully"));
        
    }
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication){
        User existingUser=(User) authentication.getPrincipal();
        AuthResponse profile=authService.getProfile(existingUser);
        return ResponseEntity.ok(profile);

    }
    @GetMapping("/test-mail")
public String testMail() throws MessagingException {
    emailService.sendHTMLMail(
        "your@email.com",
        "Test Mail",
        "If you got this, email works"
    );
    return "sent";
}

}
