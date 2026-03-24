package com.shorturl.urlshorteningservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "urls")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlShortener {

    @Id
    private String id;

    private String originalUrl;

    @Indexed(unique = true)
    private String shortCode;

    private String title;             // Optional page title / description
    private String createdBy;         // Optional user/client identifier

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;  // Optional TTL for the short URL

    private int accessCount;
    private boolean active;           // Soft-delete flag

    @Builder.Default
    private List<String> tags = new ArrayList<>();  // Optional tags/labels

    private String lastAccessedAt;    // Last time this URL was accessed
}