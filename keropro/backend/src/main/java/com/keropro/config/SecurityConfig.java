package com.keropro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Disponibiliza o BCryptPasswordEncoder para hashear senhas no cadastro e
 * verificá-las no login — sem habilitar a cadeia de filtros do Spring
 * Security (fora do escopo deste protótipo acadêmico).
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10); // fator de custo 10 — padrão recomendado
    }
}
