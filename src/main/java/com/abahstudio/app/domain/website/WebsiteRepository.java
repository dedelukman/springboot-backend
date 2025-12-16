package com.abahstudio.app.domain.website;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WebsiteRepository extends JpaRepository<Website, Long> {
    @Query(value = "SELECT * FROM websites LIMIT 1", nativeQuery = true)
    Optional<Website> findFirst();
}
