package com.abahstudio.app.domain.ticket.dto;

import com.abahstudio.app.domain.ticket.Priority;
import com.abahstudio.app.domain.ticket.TicketCategory;
import com.abahstudio.app.domain.ticket.TicketStatus;
import lombok.Builder;
import lombok.Data;

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

    // multi-company info
    private String companyCode;
}
