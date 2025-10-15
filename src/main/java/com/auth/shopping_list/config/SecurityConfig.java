package com.auth.shopping_list.config;

import com.auth.shopping_list.security.CustomUserDetailsService;
import com.auth.shopping_list.security.JwtAuthFilter;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.internal.SuppressJava6Requirement;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/create").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/users/auth/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/currencies/auth/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .userDetailsService(customUserDetailsService)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
