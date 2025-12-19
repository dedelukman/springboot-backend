package com.abahstudio.app.domain.notification;

import com.abahstudio.app.core.security.SecurityUtil;
import com.abahstudio.app.domain.notification.dto.NotificationResponse;
import com.abahstudio.app.domain.notification.dto.NotificationUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final SecurityUtil securityUtil;


    @Override
    public List<NotificationResponse> inbox() {
        return repository
                .findByCompanyCodeAndUsernameAndArchivedFalse(
                        securityUtil.getCompanyCode(),
                        securityUtil.getUsername()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<NotificationResponse> favorites() {
        return repository
                .findByCompanyCodeAndUsernameAndFavoriteTrue(
                        securityUtil.getCompanyCode(),
                        securityUtil.getUsername()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<NotificationResponse> archived() {
        return repository
                .findByCompanyCodeAndUsernameAndArchivedTrue(
                        securityUtil.getCompanyCode(),
                        securityUtil.getUsername()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public NotificationResponse update(Long id, NotificationUpdateRequest request) {

        Notification notif = repository
                .findByIdAndCompanyCode(id, securityUtil.getCompanyCode())
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // 🔐 security check
        if (!notif.getUsername().equals( securityUtil.getUsername())) {
            throw new RuntimeException("Forbidden");
        }

        if (request.getRead() != null) {
            notif.setRead(request.getRead());
        }
        if (request.getFavorite() != null) {
            notif.setFavorite(request.getFavorite());
        }
        if (request.getArchived() != null) {
            notif.setArchived(request.getArchived());
        }

        return toResponse(notif);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Notivication not found");
        }
        repository.deleteById(id);
    }

    private NotificationResponse toResponse(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.setId(n.getId());
        r.setTitle(n.getTitle());
        r.setBody(n.getBody());
        r.setType(n.getType().toString());
        r.setReferenceId(n.getReferenceId());
        r.setRead(n.getRead());
        r.setFavorite(n.getFavorite());
        r.setArchived(n.getArchived());
        r.setCreatedAt(n.getCreatedAt());
        return r;
    }
}
