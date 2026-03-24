package com.shorturl.urlshorteningservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request body for creating a new short URL.
 */
@Data
public class CreateUrlRequest {

    private String url;

    private String title;             // Optional label
    private String createdBy;         // Optional: creator identifier
    private LocalDateTime expiresAt;  // Optional: expiry datetime
    private List<String> tags;        // Optional: tags for categorization
}