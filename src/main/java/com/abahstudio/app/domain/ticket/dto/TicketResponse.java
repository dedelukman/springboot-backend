package com.abahstudio.app.domain.ticket.dto;

import com.abahstudio.app.domain.ticket.Priority;
import com.abahstudio.app.domain.ticket.TicketCategory;
import com.abahstudio.app.domain.ticket.TicketStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponse {

    private Long id;
    private String code;

    private String subject;
    private TicketCategory category;
    private Priority priority;
    private String description;
    private TicketStatus status;

    private String companyCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime updatedAt;
    private String updatedBy;
}
