package com.abahstudio.app.domain.ticket;

import com.abahstudio.app.domain.company.Company;
import com.abahstudio.app.domain.company.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final CompanyRepository companyRepository; // sementara

    @Override
    public Ticket create(Ticket ticket) {
        String code = generateTicketCode();

        // safety check (jarang bentrok, tapi best practice)
        while (ticketRepository.existsByCode(code)) {
            code = generateTicketCode();
        }

        //sementara
        Optional<Company> company = companyRepository.findByCode("AGENT0001");
        ticket.setCompany(company.get());
        ticket.setCompanyCode(company.get().getCode());
        ticket.setCode(code);
        //sementara

        ticket.setStatus(TicketStatus.OPEN);
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket update(Long id, Ticket ticket) {
        Ticket existing = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // 🔥 update field satu per satu (JPA-safe)
        existing.setSubject(ticket.getSubject());
        existing.setCategory(ticket.getCategory());
        existing.setPriority(ticket.getPriority());
        existing.setDescription(ticket.getDescription());
        existing.setStatus(ticket.getStatus());

        return ticketRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ticket> findByCode(String code) {
        return ticketRepository.findByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Ticket not found");
        }
        ticketRepository.deleteById(id);
    }

    @Override
    public List<Ticket> findAllByUser(String username) {
        return ticketRepository.findByCreatedBy(username);
    }

    private String generateTicketCode() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "T-" + LocalDateTime.now().format(formatter);
    }
}
