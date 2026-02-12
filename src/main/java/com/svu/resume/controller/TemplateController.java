package com.svu.resume.controller;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.svu.resume.service.TemplateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {
    private final TemplateService templateService;
    @GetMapping
    public ResponseEntity<?> getTemplates(Authentication authentication){
        Map<String,Object> response=templateService.getTemplates(authentication);
        return ResponseEntity.ok(response);
        
    }
}
