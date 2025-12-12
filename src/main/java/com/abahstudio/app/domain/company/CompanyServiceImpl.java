package com.abahstudio.app.domain.company;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Value("${company.code.prefix}")
    private String codePrefix;

    @Override
    public Company create(Company company) {
        if (company.getCode() == null || company.getCode().trim().isEmpty()) {

            company.setCode(generateNewCompanyCode());
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

        company.setCode(companyDetails.getCode());
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

    private String generateNewCompanyCode() {
        // 1. Dapatkan nomor urut terakhir yang digunakan
        String lastCode = companyRepository.findLastCompanyCodeByPrefix(codePrefix);

        long nextNumber = 1;
        if (lastCode != null) {
            try {
                // Ekstrak angka dari kode terakhir (contoh: dari "AGEN123" ambil "123")
                String numberPart = lastCode.substring(codePrefix.length());
                nextNumber = Long.parseLong(numberPart) + 1;
            } catch (Exception e) {
                // Tangani error jika format kode terakhir tidak sesuai ekspektasi
                System.err.println("Error parsing last company code: " + lastCode);
                nextNumber = 1; // Kembali ke 1 jika gagal parsing
            }
        }

        // 2. Format angka dengan nol di depan (misal 001, 010, 100) untuk konsistensi
        // Sesuaikan format ini (contoh: "0000" untuk 4 digit)
        DecimalFormat formatter = new DecimalFormat("0000");

        return codePrefix + formatter.format(nextNumber);
    }
}