package com.shorturl.urlshorteningservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "urls")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({

        //! Duplicate check query: findByOriginalUrlAndActiveTrue(url)
        @CompoundIndex(
                name = "idx_originalUrl_active",
                def = "{'originalUrl': 1, 'active': 1}"
        ),

        //! Top URLs query: findTop10ByActiveTrueOrderByAccessCountDesc()
        //! active=true filter + accessCount descending sort → compound index
        @CompoundIndex(
                name = "idx_active_accessCount",
                def = "{'active': 1, 'accessCount': -1}"
        )
})
public class UrlShortener {

    @Id
    private String id;

    private String originalUrl;

    @Indexed(unique = true)
    private String shortCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String userId;

    private int accessCount;
    private boolean active;

    @Builder.Default
    private boolean banned = false;

    private LocalDateTime lastAccessedAt;
}