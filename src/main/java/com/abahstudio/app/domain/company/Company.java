package com.abahstudio.app.domain.company;

import com.abahstudio.app.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "companies")
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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