package com.shorturl.urlshorteningservice.util;

import com.shorturl.urlshorteningservice.dto.UrlResponse;
import com.shorturl.urlshorteningservice.model.UrlShortener;

//! Converts UrlShortener model to UrlResponse DTO.
public class UrlMapper {

    private UrlMapper() {}

    public static UrlResponse toResponse(UrlShortener entity, String baseUrl) {
        return UrlResponse.builder()
                .id(entity.getId())
                .originalUrl(entity.getOriginalUrl())
                .shortUrl(baseUrl + "/r/" + entity.getShortCode())
                .shortCode(entity.getShortCode())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .accessCount(entity.getAccessCount())
                .active(entity.isActive())
                .build();
    }
}