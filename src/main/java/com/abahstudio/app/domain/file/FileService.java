package com.abahstudio.app.domain.file;

import com.abahstudio.app.core.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {


    private final FileRepository repository;
    private final StorageService storageService;
    private final SecurityUtil securityUtil;


    public FileEntity upload(MultipartFile file, String ownerType, String ownerId) {
        String storageKey = UUID.randomUUID().toString();
        String companyCode = securityUtil.getCompanyCode();
        storageService.save(file, storageKey);


        FileEntity entity = FileEntity.builder()
                .storageKey(storageKey)
                .originalFilename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .ownerType(ownerType)
                .ownerId(ownerId)
                .storageProvider("LOCAL")
                .build();

        entity.setCompanyCode(companyCode);


        return repository.save(entity);
    }

    public List<FileEntity> findByOwner(String ownerType, String ownerId) {
        return repository.findByOwnerTypeAndOwnerId(ownerType, ownerId);
    }

    public FileEntity findByStorageKey(String storageKey) {

        return repository
                .findByStorageKey(storageKey)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }

}
