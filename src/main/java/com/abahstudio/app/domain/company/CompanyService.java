package com.abahstudio.app.domain.company;

import com.abahstudio.app.domain.company.dto.CompanyResponse;
import com.abahstudio.app.domain.file.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyService {

    Company create(Company company);

    Company update(UUID id, Company company);

    Optional<Company> findById(UUID id);

    Optional<Company> findByCode(String code);

    List<Company> findAll();

    void delete(UUID id);

    FileEntity upload(MultipartFile file, String ownerType, String ownerId);

    Optional<CompanyResponse>  findByCodeWithLogo(String code);
}