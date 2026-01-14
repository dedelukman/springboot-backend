package com.abahstudio.app.domain.file;

import com.abahstudio.app.core.entity.CompanyScopedEntity;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FileEntity extends CompanyScopedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String storageKey; // UUID / path unik


    @Column(nullable = false)
    private String originalFilename;


    @Column(nullable = false)
    private String contentType;


    @Column(nullable = false)
    private Long size;


    @Column(nullable = false)
    private String ownerType; // USER | PRODUCT | DOCUMENT | dll


    @Column(nullable = false)
    private String ownerId; // userId / productId


    @Column(nullable = false)
    private String storageProvider; // LOCAL | S3

    private Boolean isPrimary;
}
