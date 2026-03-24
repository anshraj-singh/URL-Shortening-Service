package com.shorturl.urlshorteningservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Unified response object returned to clients for all URL operations.
 */
@Data
@Builder
public class UrlResponse {

    private String id;
    private String originalUrl;
    private String shortUrl;        // Full short URL: base + shortCode
    private String shortCode;
    private String title;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
    private int accessCount;
    private boolean active;
    private List<String> tags;
    private String lastAccessedAt;
}