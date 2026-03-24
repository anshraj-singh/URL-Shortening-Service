package com.shorturl.urlshorteningservice.repository;

import com.shorturl.urlshorteningservice.model.UrlShortener;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlShortenerRepository extends MongoRepository<UrlShortener, String> {

    Optional<UrlShortener> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    List<UrlShortener> findByCreatedBy(String createdBy);

    List<UrlShortener> findByActiveTrue();

    // All URLs whose TTL has passed
    List<UrlShortener> findByExpiresAtBefore(LocalDateTime now);

    // Tag-based search
    @Query("{ 'tags': { $in: [?0] } }")
    List<UrlShortener> findByTag(String tag);

    // Most-clicked URLs
    List<UrlShortener> findTop10ByActiveTrueOrderByAccessCountDesc();
}