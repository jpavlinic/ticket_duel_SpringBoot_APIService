package com.rit.gamifiedticketing.config;

import com.rit.gamifiedticketing.security.JwtAuthenticationFilter;
import com.rit.gamifiedticketing.security.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

import com.rit.gamifiedticketing.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final RateLimiterFilter rateLimiterFilter; // Inject Rate Limiter


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()  // Disable CSRF protection for stateless authentication
            .authorizeRequests()
            .antMatchers("/api/auth/register", "/api/auth/login", "/api/public/**", "/chat-websocket/**").permitAll()
            .anyRequest().authenticated()  // All other requests require authentication
            .and()
            .addFilterBefore(rateLimiterFilter, 
                             org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class) // Add rate limiting
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil, customUserDetailsService), 
                             org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);  // Add JWT filter before the UsernamePasswordAuthenticationFilter

        return http.build();
    }

    // Define AuthenticationManager as a Bean
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }
    
}
