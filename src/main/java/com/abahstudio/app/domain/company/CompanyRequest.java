package com.abahstudio.app.domain.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CompanyRequest {

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 150)
    private String name;

    private String address;
    private String city;
    private String postalCode;
    private String province;

    private String phone;

    @Email
    private String email;

    private String latitude;
    private String longitude;
    private String altitude;
}
