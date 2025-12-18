package com.abahstudio.app.domain.ticket;

import com.abahstudio.app.domain.ticket.dto.TicketMapper;
import com.abahstudio.app.domain.ticket.dto.TicketRequest;
import com.abahstudio.app.domain.ticket.dto.TicketResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> create(
            @RequestBody TicketRequest request
    ) {
        Ticket ticket = TicketMapper.toEntity(request);
        Ticket created = ticketService.create(ticket);
        return new ResponseEntity<>(
                TicketMapper.toResponse(created),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> update(
            @PathVariable Long id,
            @RequestBody TicketRequest request
    ) {
        Ticket ticket = TicketMapper.toEntity(request);
        Ticket updated = ticketService.update(id, ticket);
        return ResponseEntity.ok(
                TicketMapper.toResponse(updated)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> findById(@PathVariable Long id) {
        return ticketService.findById(id)
                .map(TicketMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<TicketResponse> findByCode(@PathVariable String code) {
        return ticketService.findByCode(code)
                .map(TicketMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> findAll() {
        return ResponseEntity.ok(
                ticketService.findAll()
                        .stream()
                        .map(TicketMapper::toResponse)
                        .toList()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<TicketResponse>> findAllCurrentByUser(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();
        return ResponseEntity.ok(
                ticketService.findAllByUser(username)
                        .stream()
                        .map(TicketMapper::toResponse)
                        .toList()
        );
    }
}
