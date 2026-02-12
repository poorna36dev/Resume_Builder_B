package com.svu.resume.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.svu.resume.document.Resume;
import com.svu.resume.dto.CreatedResumeRequest;
import com.svu.resume.service.FileUploadService;
import com.svu.resume.service.ResumeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {
    private final ResumeService resumeService;
    private final FileUploadService fileUploadservice;
    @PostMapping("/create")
    public ResponseEntity<?> createResume(@Valid @RequestBody CreatedResumeRequest request,Authentication authentication){
        Resume newResume=resumeService.createResume(request,authentication);
        return  ResponseEntity.status(HttpStatus.SC_CREATED).body(newResume);
    }
    @GetMapping("/fetch")
    public ResponseEntity<?> getUserResumes(Authentication authentication){
        List<Resume>resumes=resumeService.getUserResumes(authentication);
        return  ResponseEntity.ok(resumes);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getResumeById(@PathVariable String id,Authentication authentication){
        Resume resume=resumeService.getResumeById(id,authentication);
        return  ResponseEntity.ok(resume);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateResume(@PathVariable String id,@RequestBody Resume updatedData,Authentication authentication){
        Resume resume=resumeService.updateResume(id,updatedData,authentication);
        return  ResponseEntity.ok(resume);
    }
    @PutMapping("/{id}/upload-image")
    public ResponseEntity<?> uploadResumeImage(@PathVariable String id,
        @RequestPart(value="thumbnail",required=true) MultipartFile thumbnail,
        @RequestPart(value="profileImage",required=false) MultipartFile profileImage,
        Authentication authentication) throws IOException{
        Map<String,String>response=fileUploadservice.uploadResumeImages(id,authentication,thumbnail,profileImage);
        return  ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResume(@PathVariable String id,Authentication authentication){
        resumeService.deleteResume(id,authentication);
        return  ResponseEntity.ok(Map.of("message","resume deleted succesfully"));
    }


}
