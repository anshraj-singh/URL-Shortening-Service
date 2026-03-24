package com.shorturl.urlshorteningservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request body for updating an existing short URL.
 */
@Data
public class UpdateUrlRequest {

    private String newUrl;
    private String title;
    private LocalDateTime expiresAt;
    private List<String> tags;
}