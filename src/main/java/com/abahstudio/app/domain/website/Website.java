package com.abahstudio.app.domain.website;

import com.abahstudio.app.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "websites")
@Getter
@Setter
public class Website extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Core
    private String name;
    private String tagline;
    private String description;

    // SEO
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;

    // Contact
    private String email;
    private String phone;
    private String address;

}

