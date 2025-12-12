package com.abahstudio.app.domain.company;

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
}
