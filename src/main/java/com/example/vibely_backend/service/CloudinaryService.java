package com.example.vibely_backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @SuppressWarnings("unchecked")
    public Map<String, Object> uploadFile(MultipartFile file) throws IOException {
        if (cloudinary == null) {
            throw new IllegalStateException("Cloudinary bean is not initialized");
        }

        try {
            System.out.println("CloudinaryCloudinaryCloudinaryCloudinaryCloudinaryCloudinary: ");
            
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "auto"
            ));

            System.out.println("URL: " + uploadResult.get("secure_url"));
            return uploadResult;

        } catch (IOException e) {
            System.out.println("Error uploading file to Cloudinary: " + e.getMessage());
            throw new IOException("Error uploading file to Cloudinary", e);
        }
    }
    @SuppressWarnings("unchecked")
    public Map<String, Object> uploadFile(MultipartFile file, String folder) throws IOException {
        Map<String, Object> options = new HashMap<>();
        options.put("folder", folder);
        return (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), options);
    }
    
} 