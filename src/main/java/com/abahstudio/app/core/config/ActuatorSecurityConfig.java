package com.abahstudio.app.core.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ActuatorSecurityConfig {

    @Bean
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher(EndpointRequest.toAnyEndpoint()) // Applies rules only to Actuator endpoints
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll() // Untuk load balancer
                        .requestMatchers("/actuator/info").permitAll()   // Untuk info versi
                        .requestMatchers("/actuator/**").hasRole("ACTUATOR_ADMIN") // Sisanya butuh role
                        .anyRequest().authenticated() // Aturan untuk aplikasi utama
                );
        return http.build();
    }
}