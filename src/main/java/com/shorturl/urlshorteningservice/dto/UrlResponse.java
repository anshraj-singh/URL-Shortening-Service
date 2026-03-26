package com.shorturl.urlshorteningservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

//! Response object returned to clients for all URL operations.
@Data
@Builder
public class UrlResponse {

    private String id;
    private String originalUrl;
    private String shortUrl;
    private String shortCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int accessCount;
    private boolean active;
}