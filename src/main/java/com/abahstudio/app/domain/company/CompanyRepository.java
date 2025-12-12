package com.abahstudio.app.domain.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Optional<Company> findByCode(String code);

    boolean existsByCode(String code);

    @Query(value = "SELECT c.code FROM Company c WHERE c.code LIKE CONCAT(?1, '%') ORDER BY c.code DESC LIMIT 1")
    String findLastCompanyCodeByPrefix(String prefix);

}