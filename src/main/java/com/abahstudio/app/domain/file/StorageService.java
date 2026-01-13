package com.abahstudio.app.domain.file;

import org.springframework.web.multipart.MultipartFile;


import java.io.InputStream;


public interface StorageService {
    String save(MultipartFile file, String storageKey);
    InputStream load(String storageKey);
    void delete(String storageKey);
}
