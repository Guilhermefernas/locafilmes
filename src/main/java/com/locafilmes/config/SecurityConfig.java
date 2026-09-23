package com.locafilmes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança desta etapa (CRUDs básicos e relacionamentos).
 *
 * Por enquanto os endpoints ficam liberados (permitAll) para facilitar os testes
 * no Postman/Swagger. O bean de PasswordEncoder (BCrypt) já é usado para nunca
 * gravar a senha do usuário em texto puro (RNF03).
 *
 * Na próxima etapa (JWT + Spring Security) este arquivo será evoluído com:
 *  - filtro de autenticação JWT;
 *  - restrição por Role (ADMIN x CLIENTE) usando @PreAuthorize / authorizeHttpRequests.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
