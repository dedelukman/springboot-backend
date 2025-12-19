package com.abahstudio.app.domain.notification.dto;

import lombok.Data;

@Data
public class NotificationUpdateRequest {

    private Boolean read;
    private Boolean favorite;
    private Boolean archived;
}
