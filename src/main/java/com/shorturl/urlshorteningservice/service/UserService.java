package com.shorturl.urlshorteningservice.service;

import com.shorturl.urlshorteningservice.dto.AuthResponse;
import com.shorturl.urlshorteningservice.dto.LoginRequest;
import com.shorturl.urlshorteningservice.dto.RegisterRequest;
import com.shorturl.urlshorteningservice.dto.RegisterResponse;
import com.shorturl.urlshorteningservice.model.User;
import com.shorturl.urlshorteningservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByName(request.getName())) {
            throw new RuntimeException("Name already taken: " + request.getName());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", request.getName());

        return RegisterResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .createdAt(savedUser.getCreatedAt())
                .message("Account created successfully. Please login to get your token.")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUser(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByName(request.getUser())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUser()));

        log.info("User logged in: {}", request.getUser());
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }
}