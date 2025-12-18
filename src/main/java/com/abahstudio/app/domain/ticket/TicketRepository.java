package com.abahstudio.app.domain.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByCode(String code);
    @Query("SELECT t FROM Ticket t  WHERE t.createdBy = ?1")
    List<Ticket> findByCreatedBy(String username);
    boolean existsByCode(String code);
}
