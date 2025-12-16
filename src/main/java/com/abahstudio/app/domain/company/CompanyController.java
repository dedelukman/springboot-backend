package com.abahstudio.app.domain.company;


import com.abahstudio.app.domain.company.dto.CompanyMapper;
import com.abahstudio.app.domain.company.dto.CompanyRequest;
import com.abahstudio.app.domain.company.dto.CompanyResponse;
import com.abahstudio.app.domain.user.User;
import com.abahstudio.app.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final UserService userService;
    private final CompanyService companyService;
    private final CompanyMapper mapper;

    // CREATE
    @PostMapping
    public ResponseEntity<CompanyResponse> create(@Validated @RequestBody CompanyRequest request) {
        Company entity = mapper.toEntity(request);
        Company saved = companyService.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(saved));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAll() {
        List<CompanyResponse> list = companyService.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(list);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getById(@PathVariable UUID id) {
        return companyService.findById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> update(
            @PathVariable UUID id,
            @Validated @RequestBody CompanyRequest request) {

        Company existing = companyService.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        mapper.updateEntity(request, existing);

        Company updated = companyService.update(id, existing);

        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<CompanyResponse> getCurrentCompany(Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = auth.getName();
        log.info(username);
        Optional<User> userOptional = userService.getUserByUsername(username);
       String code = userOptional.get().getCompanyCode();
        return companyService.findByCode(code)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}