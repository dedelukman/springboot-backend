package com.abahstudio.app.domain.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationProducerImpl implements NotificationProducer {

    private final NotificationRepository repository;

    @Override
    public void notify(
            String companyCode,
            String username,
            NotificationType type,
            String title,
            String body,
            String referenceId
    ) {
        Notification notif = new Notification();
        notif.setCompanyCode(companyCode);
        notif.setUsername(username);
        notif.setType(type);
        notif.setTitle(title);
        notif.setBody(body);
        notif.setReferenceId(referenceId);

        notif.setRead(false);
        notif.setFavorite(false);
        notif.setArchived(false);

        repository.save(notif);
    }
}
