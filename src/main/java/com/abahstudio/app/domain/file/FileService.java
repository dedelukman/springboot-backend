package com.abahstudio.app.domain.file;

import com.abahstudio.app.core.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
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

    public Optional<FileEntity> findPrimaryByOwner(String ownerType, String ownerId) {
        return repository
                .findFirstByOwnerTypeAndOwnerIdAndIsPrimaryTrue(ownerType, ownerId);
    }


    public FileEntity findByStorageKey(String storageKey) {
        return repository
                .findByStorageKey(storageKey)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }

    @Transactional
    public void delete(FileEntity file) {
        repository.delete(file);
        storageService.delete(file.getStorageKey());
    }

    public FileEntity save(FileEntity entity) {
        return repository.save(entity);
    }


}
