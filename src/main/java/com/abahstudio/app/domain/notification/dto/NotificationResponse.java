package com.abahstudio.app.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {

    private Long id;
    private String title;
    private String body;
    private String type;
    private String referenceId;
    private Boolean read;
    private Boolean favorite;
    private Boolean archived;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;

}
