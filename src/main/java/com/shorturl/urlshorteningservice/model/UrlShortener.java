package com.shorturl.urlshorteningservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

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

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int accessCount;
    private boolean active;
}