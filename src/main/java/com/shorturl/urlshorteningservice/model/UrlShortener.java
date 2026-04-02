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

/**
 * MongoDB Indexes explained:
 *
 * 1. shortCode        → unique index
 *    Every redirect hits findByShortCode() — most frequent query.
 *    Unique = no two URLs can have same short code.
 *    Without index: full collection scan every redirect.
 *    With index:    instant lookup, like a dictionary.
 *
 * 2. originalUrl + active  → compound index
 *    Used by duplicate URL check: findByOriginalUrlAndActiveTrue()
 *    Compound = both fields searched together in one index.
 *    Without index: scan all records, check both fields one by one.
 *    With index:    jump directly to matching records.
 *
 * 3. active + accessCount  → compound index
 *    Used by top URLs query: findTop10ByActiveTrueOrderByAccessCountDesc()
 *    MongoDB uses this index to sort without scanning all records.
 *    Without index: fetch all → sort in memory → slow on large data.
 *    With index:    pre-sorted, instant top 10.
 */
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

    private int accessCount;
    private boolean active;
}