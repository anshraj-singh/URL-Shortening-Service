package com.shorturl.urlshorteningservice.util;

import com.shorturl.urlshorteningservice.dto.UrlResponse;
import com.shorturl.urlshorteningservice.model.UrlShortener;

/**
 * Converts UrlShortener model → UrlResponse DTO.
 * Centralises the mapping so controllers and services don't repeat it.
 */
public class UrlMapper {

    private UrlMapper() {}  // Utility class — no instances needed

    public static UrlResponse toResponse(UrlShortener entity, String baseUrl) {
        return UrlResponse.builder()
                .id(entity.getId())
                .originalUrl(entity.getOriginalUrl())
                .shortUrl(baseUrl + "/r/" + entity.getShortCode())
                .shortCode(entity.getShortCode())
                .title(entity.getTitle())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .expiresAt(entity.getExpiresAt())
                .accessCount(entity.getAccessCount())
                .active(entity.isActive())
                .tags(entity.getTags())
                .lastAccessedAt(entity.getLastAccessedAt())
                .build();
    }
}