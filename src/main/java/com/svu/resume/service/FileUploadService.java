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
public class FileUploadService {

    private final Cloudinary cloudinary;
    private final AuthService authService;
    private final ResumeRepository resumeRepository;

    public Map<String, String> uploadSingleImage(MultipartFile file) throws IOException {
        Map<String, Object> imageUploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "image"));
        String result = imageUploadResult.get("secure_url").toString();
        return Map.of("imageUrl", result);

    }

    public Map<String, String> uploadResumeImages(String id, Authentication authentication, MultipartFile thumbnail,
            MultipartFile profileImage) throws IOException {
        User user = (User) authentication.getPrincipal();
        AuthResponse response = authService.getProfile(user);
        Resume existingResume = resumeRepository
                .findByUserIdAndId(response.getId(), id)
                .orElseThrow(() -> {
                    return new RuntimeException("Resume not Found");
                });
        Map<String, String> returnValue = new HashMap<>();
        Map<String, String> uploadResult;
        if (Objects.nonNull(thumbnail)) {
            uploadResult = uploadSingleImage(thumbnail);
            existingResume.setThumbnailLink(uploadResult.get("imageUrl"));
            returnValue.put("thumbnailLink", uploadResult.get("imageUrl"));
        }
        if (Objects.nonNull(profileImage)) {
            uploadResult = uploadSingleImage(thumbnail);
            if (Objects.isNull(existingResume.getProfileInfo())) {
                existingResume.setProfileInfo(new Resume.ProfileInfo());
            }
            existingResume.getProfileInfo().setProfilePreviewUrl(uploadResult.get("imageUrl"));
            returnValue.put("profilePreviewUrl", uploadResult.get("imageUrl"));
        }
        resumeRepository.save(existingResume);
        returnValue.put("message", "images upload succesfully");
        return returnValue;
    }

    public String uploadResumePdf(MultipartFile file) throws IOException {
        // resource_type: "raw" is required for non-image files like PDF when you want
        // them treated as files
        // folder: "resumes" to keep them organized
        Map<String, Object> params = new HashMap<>();
        params.put("resource_type", "raw");
        params.put("folder", "resumes");

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

        // Generate a URL that streams the file as an attachment (forces download)
        // We use the public_id from the upload result
        String publicId = (String) uploadResult.get("public_id");

        // Construct the URL manually or use the url() builder if available to ensure
        // correct flags
        // For raw files, we generally want the secure_url.
        // To force download, we can append fl_attachment to the transformation or URL.
        // However, for "raw" resources, standard transformations like fl_attachment
        // might need
        // to be part of the URL generation specifically for raw types or just appended.
        // A reliable way for raw files on Cloudinary to force download is using the
        // "fl_attachment" flag.

        return cloudinary.url()
                .resourceType("raw")
                .transformation(new com.cloudinary.Transformation().flags("attachment"))
                .secure(true)
                .generate(publicId);
    }
}
