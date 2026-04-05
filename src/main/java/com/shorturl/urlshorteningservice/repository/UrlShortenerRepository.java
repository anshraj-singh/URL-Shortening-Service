package com.shorturl.urlshorteningservice.repository;

import com.shorturl.urlshorteningservice.model.UrlShortener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlShortenerRepository extends MongoRepository<UrlShortener, String> {

    Optional<UrlShortener> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    //! Duplicate check
    Optional<UrlShortener> findByOriginalUrlAndActiveTrueAndUserId(String originalUrl, String userId);

    List<UrlShortener> findByActiveTrue();

    // Top 10 most clicked
    List<UrlShortener> findTop10ByActiveTrueOrderByAccessCountDesc();

    // Admin — paginated all URLs
    Page<UrlShortener> findAll(Pageable pageable);

    List<UrlShortener> findByAccessCountGreaterThanAndLastAccessedAtAfter(
            int accessCount, LocalDateTime since);

    // Admin — banned URLs
    List<UrlShortener> findByBannedTrue();

    // Bulk delete
    List<UrlShortener> findByShortCodeIn(List<String> shortCodes);
}