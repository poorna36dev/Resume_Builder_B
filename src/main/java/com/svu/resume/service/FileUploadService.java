package com.svu.resume.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.svu.resume.document.Resume;
import com.svu.resume.document.User;
import com.svu.resume.dto.AuthResponse;
import com.svu.resume.repository.ResumeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileUploadService{
    
    private final Cloudinary cloudinary;
    private final AuthService authService;
    private final ResumeRepository resumeRepository;
    public Map<String,String> uploadSingleImage(MultipartFile file) throws IOException{
       Map<String,Object>imageUploadResult= cloudinary.uploader().upload(file.getBytes(),ObjectUtils.asMap("resource_type","image"));
       String result=imageUploadResult.get("secure_url").toString();
       return Map.of("imageUrl",result);

    }

    public Map<String, String> uploadResumeImages(String id, Authentication authentication, MultipartFile thumbnail, MultipartFile profileImage) throws IOException {
        User user = (User) authentication.getPrincipal(); 
        AuthResponse response=authService.getProfile(user);
        Resume existingResume=resumeRepository
        .findByUserIdAndId(response.getId(),id)
        .orElseThrow(()->{
            return new RuntimeException("Resume not Found");
        });
        Map<String,String> returnValue=new HashMap<>();
        Map<String,String> uploadResult;
        if(Objects.nonNull(thumbnail)){
            uploadResult=uploadSingleImage(thumbnail); 
            existingResume.setThumbnailLink(uploadResult.get("imageUrl"));
            returnValue.put("thumbnailLink",uploadResult.get("imageUrl"));
        }
        if(Objects.nonNull(profileImage)){
            uploadResult=uploadSingleImage(thumbnail);
            if(Objects.isNull(existingResume.getProfileInfo())){
                existingResume.setProfileInfo(new Resume.ProfileInfo());
            }
            existingResume.getProfileInfo().setProfilePreviewUrl(uploadResult.get("imageUrl"));
            returnValue.put("profilePreviewUrl",uploadResult.get("imageUrl"));
        }
        resumeRepository.save(existingResume);
        returnValue.put("message","images upload succesfully");
        return returnValue;
    }
}
