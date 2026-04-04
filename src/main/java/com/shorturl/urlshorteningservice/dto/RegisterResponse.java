package com.shorturl.urlshorteningservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RegisterResponse {
    private String id;
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private String message;
}