package com.abahstudio.app.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class CompanyScopedEntity extends BaseEntity {

    @Column(nullable = false)
    private String companyCode;
}
