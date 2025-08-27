package com.crus.customerWebsite.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
                // disable CSRF for Postman usage
                .csrf(csrf -> csrf.disable())
                // permit all requests to access CSS, JavaScript, images, and login
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/webjars/**", "/css/**", "/login/**", "/images/**", "/js", "/register").permitAll()
                        .requestMatchers("/customer-view").hasRole("USER")
                        .requestMatchers("/customer-list").hasRole("ADMIN")
                        .anyRequest().authenticated())
                        .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

