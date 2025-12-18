package com.abahstudio.app.domain.ticket;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    Ticket create(Ticket ticket);

    Ticket update(Long id, Ticket ticket);

    Optional<Ticket> findById(Long id);

    Optional<Ticket> findByCode(String code);

    List<Ticket> findAll();

    void delete(Long id);

    List<Ticket> findAllByUser(String username);
}
