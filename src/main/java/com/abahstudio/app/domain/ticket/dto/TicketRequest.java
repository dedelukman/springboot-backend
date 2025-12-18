package com.abahstudio.app.domain.ticket.dto;

import com.abahstudio.app.domain.ticket.Priority;
import com.abahstudio.app.domain.ticket.TicketCategory;
import com.abahstudio.app.domain.ticket.TicketStatus;
import lombok.Data;

@Data
public class TicketRequest {

    private String subject;
    private TicketCategory category;
    private Priority priority;
    private String description;

    // opsional (dipakai saat update)
    private TicketStatus status;
}
