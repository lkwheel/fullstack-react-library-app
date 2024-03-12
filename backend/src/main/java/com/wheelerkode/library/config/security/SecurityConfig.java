package com.wheelerkode.library.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthenticationErrorHandler authenticationErrorHandler;

    @Bean
    public SecurityFilterChain httpSecurity(final HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authz -> authz.requestMatchers(
                    "/api/books/protected/**",
                    "/api/reviews/protected/**",
                    "/api/histories/protected/**",
                    "/api/messages/protected/**"
                ).authenticated().anyRequest().permitAll())
                .cors(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(authenticationErrorHandler)).build();
    }
}
