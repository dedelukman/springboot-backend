package com.abahstudio.app.domain.notification;

import com.abahstudio.app.domain.notification.dto.NotificationResponse;
import com.abahstudio.app.domain.notification.dto.NotificationUpdateRequest;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse> inbox();

    List<NotificationResponse> favorites();

    List<NotificationResponse> archived();

    NotificationResponse update(Long id, NotificationUpdateRequest request);

    void delete(Long id);
}
