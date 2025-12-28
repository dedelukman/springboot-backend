package com.abahstudio.app.core.initializer;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.super-admin")
public class SuperAdminProperties {

    private String email;
    private String username;
    private String password;
}
