package com.example.demo.config;

import org.springframework.boot.test.context.TestConfiguration; // ⬅️ IMPORTANTE: Cambia a TestConfiguration
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // ⬅️ IMPORTANTE: Agrega esto
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration // ⬅️ CAMBIO 1: Era @Configuration, ahora @TestConfiguration
@EnableWebSecurity // ⬅️ CAMBIO 2: FALTABA esta anotación
@Profile("test")
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Forma moderna
                .cors(AbstractHttpConfigurer::disable) // Desactiva CORS también
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable) // Desactiva form login
                .httpBasic(AbstractHttpConfigurer::disable); // Desactiva basic auth

        return http.build();
    }

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

