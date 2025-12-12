package com.abahstudio.app.domain.company;

import com.abahstudio.app.core.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "companies")
public class Company extends BaseEntity {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
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

}
