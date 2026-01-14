package com.abahstudio.app.domain.company.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CompanyResponse {

    private String id;
    private String code;
    private String name;

    private String address;
    private String city;
    private String postalCode;
    private String province;

    private String phone;
    private String email;

    private String latitude;
    private String longitude;
    private String altitude;

    private String createdAt;
    private String updatedAt;

    private String logo;
}