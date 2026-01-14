package com.abahstudio.app.domain.company;

import com.abahstudio.app.core.numbering.NumberingService;
import com.abahstudio.app.domain.company.dto.CompanyMapper;
import com.abahstudio.app.domain.company.dto.CompanyResponse;
import com.abahstudio.app.domain.file.FileEntity;
import com.abahstudio.app.domain.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final NumberingService numberingService;
    private final FileService fileService;
    private final CompanyMapper mapper;


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

    @Override
    public FileEntity upload(MultipartFile file, String ownerType, String ownerId) {
        Optional<FileEntity> oldLogo =
                fileService.findPrimaryByOwner(ownerType, ownerId);

        FileEntity newLogo = fileService.upload(file, ownerType, ownerId);

        newLogo.setIsPrimary(true);
        FileEntity savedLogo = fileService.save(newLogo);

        oldLogo.ifPresent(fileService::delete);

        return savedLogo;
    }

    @Override
    public Optional<CompanyResponse> findByCodeWithLogo(String code) {
        return companyRepository.findByCode(code)
                .map(company -> {
                    CompanyResponse res = mapper.toResponse(company);

                    fileService.findByOwner("COMPANY", code)
                            .stream()
                            .findFirst()
                            .ifPresent(file -> {
                                res.setLogo(
                                        "files/view/" + file.getStorageKey()
                                );
                            });

                    return res;
                });
    }

}