package com._p1m.portfolio.data.storage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;

@Service
@Profile("clouinary")
@RequiredArgsConstructor
public class CloudinaryAdapter implements CloudStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String upload(MultipartFile file) {
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("folder", "opom_portfolio");
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
            String publicId = (String) uploadedFile.get("public_id");
            return cloudinary.url().secure(true).generate(publicId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload to Cloudinary", e);
        }
    }

    @Override
    public String getImageUrl(String publicId) {
        return cloudinary.url()
                .secure(true)
                .generate(publicId);
    }

    @Override
    public void delete(String publicId) {
        try {
            Map<String, Object> deleteOptions = new HashMap<>();
            deleteOptions.put("invalidate", true);
            this.cloudinary.uploader().destroy(publicId, deleteOptions);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete from Cloudinary", e);
        }
    }

    @Override
    public String update(MultipartFile newFile, String publicId, String folderName) {
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("public_id", folderName + "/" + publicId);
            options.put("overwrite", true);

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadedFile = (Map<String, Object>) cloudinary.uploader().upload(newFile.getBytes(), options);
            return (String) uploadedFile.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to update image on Cloudinary", e);
        }
    }
}