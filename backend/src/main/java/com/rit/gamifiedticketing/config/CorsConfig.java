package com.rit.gamifiedticketing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        
        // Allow specific origins (Update this based on your frontend domain)
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://solace.ist.rit.edu/~jl8592")); 
        
        // Allow standard HTTP methods
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Allow standard headers
        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // Allow credentials (if frontend sends cookies/JWTs)
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }
}
