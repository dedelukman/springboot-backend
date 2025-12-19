package com.abahstudio.app.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByCompanyCodeAndUsernameAndArchivedFalse(
            String companyCode,
            String username
    );

    List<Notification> findByCompanyCodeAndUsernameAndFavoriteTrue(
            String companyCode,
            String username
    );

    List<Notification> findByCompanyCodeAndUsernameAndArchivedTrue(
            String companyCode,
            String username
    );

    Optional<Notification> findByIdAndCompanyCode(Long id, String companyCode);
}

