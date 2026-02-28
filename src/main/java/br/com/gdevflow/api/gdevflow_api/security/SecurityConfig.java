package br.com.gdevflow.api.gdevflow_api.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// Classe responsável pelas configurações de segurança da aplicação
@Configuration
public class SecurityConfig {

    // Define a cadeia de filtros de segurança do Spring Security
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Habilita CORS utilizando a configuração global definida abaixo
                .cors(Customizer.withDefaults())

                // Desabilita CSRF pois a aplicação é uma API REST stateless
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Define as regras de autorização dos endpoints
                .authorizeHttpRequests(auth -> auth
                        // Endpoint público para health check, registro e tratamento de erros
                        .requestMatchers("/health", "/auth/register", "/auth/login", "/error", "/error/**").permitAll()
                        // Qualquer outro endpoint exige autenticação
                        .anyRequest().authenticated()
                )
                
                .build();
    }

    // Configuração global de CORS da aplicação
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Aceita todas as origens
        config.setAllowedOriginPatterns(List.of("*"));

        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Headers permitidos nas requisições
        config.setAllowedHeaders(List.of("*"));

        // Permite envio de credenciais (cookies, headers de autenticação)
        config.setAllowCredentials(false);

        // Aplica a configuração de CORS para todos os endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}