package com.svu.resume.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.svu.resume.document.Resume;
import com.svu.resume.document.User;
import com.svu.resume.dto.AuthResponse;
import com.svu.resume.dto.CreatedResumeRequest;
import com.svu.resume.repository.ResumeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final AuthService authService;
    public Resume createResume(CreatedResumeRequest request,Authentication authentication) {
        Resume newResume=new Resume();
        User user = (User) authentication.getPrincipal(); 
        AuthResponse response=authService.getProfile(user);
        newResume.setUserId(response.getId());
        newResume.setTitle(request.getTitle());
        setDefaultInfo(newResume);
        return resumeRepository.save(newResume);
        
    }

    private void setDefaultInfo(Resume newResume) {
        newResume.setProfileInfo(new Resume.ProfileInfo());
        newResume.setContactInfo(new Resume.ContactInfo());
        newResume.setWorkExperiences(new ArrayList<>());
        newResume.setSkills(new ArrayList<>());
        newResume.setProjects(new ArrayList<>());
        newResume.setEducation(new ArrayList<>());
        newResume.setCertifications(new ArrayList<>());
        newResume.setLanguages(new ArrayList<>());
        newResume.setHobbies(new ArrayList<>());
    }

    public List<Resume> getUserResumes(Authentication authentication) {
       User user = (User) authentication.getPrincipal(); 
        AuthResponse response=authService.getProfile(user);
        List<Resume> resumes= resumeRepository.findByUserIdOrderByUpdatedAtDesc(response.getId());
        return resumes;
    }

    public Resume getResumeById(String id, Authentication authentication) {
        User user = (User) authentication.getPrincipal(); 
        AuthResponse response=authService.getProfile(user);
        Resume resume=resumeRepository
        .findByUserIdAndId(response.getId(),id)
        .orElseThrow(()->{
            return new RuntimeException("Resume not Found");
        });
        return resume;
    }

    public Resume updateResume(String id, Resume updatedData, Authentication authentication) {
        User user = (User) authentication.getPrincipal(); 
        AuthResponse response=authService.getProfile(user);
        Resume existingResume=resumeRepository
        .findByUserIdAndId(response.getId(),id)
        .orElseThrow(()->{
            return new RuntimeException("Resume not Found");
        });  
        existingResume.setSkills(updatedData.getSkills());
        existingResume.setProfileInfo(updatedData.getProfileInfo());
        existingResume.setThumbnailLink(updatedData.getThumbnailLink());
        existingResume.setTemplate(updatedData.getTemplate());
        existingResume.setContactInfo(updatedData.getContactInfo());
        existingResume.setWorkExperiences(updatedData.getWorkExperiences());
        existingResume.setEducation(updatedData.getEducation());
        existingResume.setProjects(updatedData.getProjects());
        existingResume.setCertifications(updatedData.getCertifications());
        existingResume.setLanguages(updatedData.getLanguages());
        existingResume.setHobbies(updatedData.getHobbies());
        Resume resume=resumeRepository.save(existingResume);
        return resume;
        
    }

    public void deleteResume(String id, Authentication authentication) {
        User user = (User) authentication.getPrincipal(); 
        AuthResponse response=authService.getProfile(user);
        Resume existingResume=resumeRepository
        .findByUserIdAndId(response.getId(),id)
        .orElseThrow(()->{
            return new RuntimeException("Resume not Found");
        });
        resumeRepository.delete(existingResume); 
    }
}
