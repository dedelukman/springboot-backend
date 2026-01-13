package com.abahstudio.app.domain.file;

import com.abahstudio.app.core.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;


import java.io.*;
import java.nio.file.*;


@Service
public class LocalStorageService implements StorageService {

    @Value("${app.file.upload-dir:uploads}")
    private String basePath;

    private final SecurityUtil securityUtil;

    public LocalStorageService(SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }

    private String getCompanyBasedPath(String storageKey) {
        String companyCode = securityUtil.getCompanyCode();
        if (companyCode == null || companyCode.trim().isEmpty()) {
            throw new RuntimeException("Company code not available for file operation");
        }

        // Membersihkan dan memastikan path aman
        String cleanCompanyCode = companyCode.replaceAll("[^a-zA-Z0-9._-]", "_");
        return cleanCompanyCode + "/" + storageKey;
    }


    @Override
    public String save(MultipartFile file, String storageKey) {
        try {
            String companyBasedKey = getCompanyBasedPath(storageKey);
            Path target = Paths.get(basePath).resolve(companyBasedKey);
            Files.createDirectories(target.getParent());
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return companyBasedKey;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }


    @Override
    public InputStream load(String storageKey) {
        try {
            String companyBasedKey = getCompanyBasedPath(storageKey);
            return Files.newInputStream(Paths.get(basePath).resolve(companyBasedKey));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file", e);
        }
    }


    @Override
    public void delete(String storageKey) {
        try {
            String companyBasedKey = getCompanyBasedPath(storageKey);
            Files.deleteIfExists(Paths.get(basePath).resolve(companyBasedKey));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }
}
