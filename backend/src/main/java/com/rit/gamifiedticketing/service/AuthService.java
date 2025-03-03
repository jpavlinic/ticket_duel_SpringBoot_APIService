package com.rit.gamifiedticketing.service;

import com.rit.gamifiedticketing.dto.UserDTO;
import com.rit.gamifiedticketing.entity.User;
import com.rit.gamifiedticketing.repository.UserRepository;
import com.rit.gamifiedticketing.security.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserDTO registerUser(
            @NotBlank @Size(min = 4, max = 50) String username,
            @Email @NotBlank @Size(max = 100) String email,
            @NotBlank @Size(min = 8, message = "Password must be at least 8 characters long") String password,
            @NotBlank @Pattern(regexp = "^(ADMIN|QUESTIONNAIRE|SOLVER)$", message = "Role must be one of: ADMIN, QUESTIONNAIRE, SOLVER") String role) { // Added
                                                                                                                                                        // role

        // Trim whitespace and remove any malicious scripts (XSS protection)
        username = sanitizeInput(username);
        email = sanitizeInput(email);

        // Check if username or email already exists
        Optional<User> existingUser = userRepository.findByUsername(username);
        Optional<User> existingEmail = userRepository.findByEmail(email);

        if (existingUser.isPresent() || existingEmail.isPresent()) {
            throw new IllegalArgumentException("Username or Email already in use.");
        }

        // Create new user and hash password
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role); // Default role

        // Save user to database
        userRepository.save(user);

        // Return sanitized DTO (without password)
        return new UserDTO(user.getUsername(), user.getEmail(), user.getPoints());
    }

    @Transactional
    public Map<String, String> loginUser(@NotBlank String username, @NotBlank String password) {
        // Authenticate with Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    
        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    
            // Extract the role(s) from the authenticated user
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER"); // Default role if none found
    
            // Generate JWT token
            String token = jwtTokenUtil.generateToken(userDetails.getUsername());
    
            // Create response map
            Map<String, String> response = new HashMap<>();
            response.put("token", "Bearer " + token);
            response.put("role", role);
            response.put("username", userDetails.getUsername()); // Add username to response
            return response;
        } else {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }
    

    private String sanitizeInput(String input) {
        if (input == null)
            return null;
        return Jsoup.clean(input.trim(), Safelist.none()); // Removes all HTML tags & scripts
    }
}
