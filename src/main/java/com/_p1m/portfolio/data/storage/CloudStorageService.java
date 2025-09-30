package com._p1m.portfolio.data.storage;

import org.springframework.web.multipart.MultipartFile;

public interface CloudStorageService {
    String upload(MultipartFile file);
    
    public String getImageUrl(String publicId);
    
    void delete(String filename);

    String update(MultipartFile newFile, String publicId, String folderName);
}
