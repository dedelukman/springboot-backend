package com.abahstudio.app.domain.company;

import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    // CREATE: Request -> Entity
    public Company toEntity(CompanyRequest request) {
        if (request == null) return null;

        Company company = new Company();
        company.setCode(request.getCode());
        company.setName(request.getName());
        company.setAddress(request.getAddress());
        company.setCity(request.getCity());
        company.setPostalCode(request.getPostalCode());
        company.setProvince(request.getProvince());
        company.setPhone(request.getPhone());
        company.setEmail(request.getEmail());
        company.setLatitude(request.getLatitude());
        company.setLongitude(request.getLongitude());
        company.setAltitude(request.getAltitude());

        return company;
    }

    // UPDATE: Request -> Existing Entity
    public void updateEntity(CompanyRequest request, Company company) {
        if (request == null || company == null) return;

        // Hanya update field yang tidak null (safe update)
        if (request.getCode() != null) company.setCode(request.getCode());
        if (request.getName() != null) company.setName(request.getName());
        if (request.getAddress() != null) company.setAddress(request.getAddress());
        if (request.getCity() != null) company.setCity(request.getCity());
        if (request.getPostalCode() != null) company.setPostalCode(request.getPostalCode());
        if (request.getProvince() != null) company.setProvince(request.getProvince());
        if (request.getPhone() != null) company.setPhone(request.getPhone());
        if (request.getEmail() != null) company.setEmail(request.getEmail());
        if (request.getLatitude() != null) company.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) company.setLongitude(request.getLongitude());
        if (request.getAltitude() != null) company.setAltitude(request.getAltitude());
    }

    // Entity -> Response
    public CompanyResponse toResponse(Company company) {
        if (company == null) return null;

        CompanyResponse response = new CompanyResponse();
        response.setId(company.getId() != null ? company.getId().toString() : null);
        response.setCode(company.getCode());
        response.setName(company.getName());
        response.setAddress(company.getAddress());
        response.setCity(company.getCity());
        response.setPostalCode(company.getPostalCode());
        response.setProvince(company.getProvince());
        response.setPhone(company.getPhone());
        response.setEmail(company.getEmail());
        response.setLatitude(company.getLatitude());
        response.setLongitude(company.getLongitude());
        response.setAltitude(company.getAltitude());
        response.setCreatedAt(company.getCreatedAt() != null ? company.getCreatedAt().toString() : null);
        response.setUpdatedAt(company.getUpdatedAt() != null ? company.getUpdatedAt().toString() : null);

        return response;
    }
}