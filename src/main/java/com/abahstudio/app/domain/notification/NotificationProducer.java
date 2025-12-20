package com.abahstudio.app.domain.notification;


public interface NotificationProducer {

    void notify(
            String companyCode,
            String username,
            NotificationType type,
            String title,
            String body,
            String referenceId
    );
}
