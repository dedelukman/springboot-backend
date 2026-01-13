package com.abahstudio.app.domain.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    Optional<FileEntity> findByStorageKey(String storageKey);
    List<FileEntity> findByOwnerTypeAndOwnerId(String ownerType, String ownerId);
}
