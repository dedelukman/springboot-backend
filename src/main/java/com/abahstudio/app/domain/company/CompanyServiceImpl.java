package com.abahstudio.app.domain.company;

import com.abahstudio.app.core.numbering.NumberingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final NumberingService numberingService;


    @Override
    public Company create(Company company) {
        if (company.getCode() == null || company.getCode().trim().isEmpty()) {

            company.setCode(numberingService.generateNumber("COMPANY_BY_SYSTEM"));
        } else {

            if (companyRepository.existsByCode(company.getCode())) {
                throw new RuntimeException("Company code already exists: " + company.getCode());
            }
        }

        return companyRepository.save(company);
    }

    @Override
    public Company update(UUID id, Company companyDetails) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));

        // Jika code berubah, cek apakah sudah ada
        if (!company.getCode().equals(companyDetails.getCode())
                && companyRepository.existsByCode(companyDetails.getCode())) {
            throw new RuntimeException("Company code already exists: " + companyDetails.getCode());
        }

        company.setName(companyDetails.getName());
        company.setAddress(companyDetails.getAddress());
        company.setCity(companyDetails.getCity());
        company.setPostalCode(companyDetails.getPostalCode());
        company.setProvince(companyDetails.getProvince());
        company.setPhone(companyDetails.getPhone());
        company.setEmail(companyDetails.getEmail());
        company.setLatitude(companyDetails.getLatitude());
        company.setLongitude(companyDetails.getLongitude());
        company.setAltitude(companyDetails.getAltitude());

        return companyRepository.save(company);
    }

    @Override
    public Optional<Company> findById(UUID id) {
        return companyRepository.findById(id);
    }

    @Override
    public Optional<Company> findByCode(String code) {
        return companyRepository.findByCode(code);
    }

    @Override
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    @Override
    public void delete(UUID id) {
        companyRepository.deleteById(id);
    }

}