package com.abahstudio.app.domain.ticket.dto;


import com.abahstudio.app.domain.ticket.Ticket;

public class TicketMapper {

    public static Ticket toEntity(TicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setSubject(request.getSubject());
        ticket.setCategory(request.getCategory());
        ticket.setPriority(request.getPriority());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(request.getStatus());
        return ticket;
    }

    public static TicketResponse toResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .code(ticket.getCode())
                .subject(ticket.getSubject())
                .category(ticket.getCategory())
                .priority(ticket.getPriority())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .companyCode(ticket.getCompanyCode())
                .build();
    }
}

