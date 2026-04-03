package com.shorturl.urlshorteningservice.repository;

import com.shorturl.urlshorteningservice.model.UrlShortener;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlShortenerRepository extends MongoRepository<UrlShortener, String> {

    Optional<UrlShortener> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    //! Duplicate check
    Optional<UrlShortener> findByOriginalUrlAndActiveTrueAndUserId(String originalUrl, String userId);

    List<UrlShortener> findByUserId(String userId);
    List<UrlShortener> findByUserIdAndActiveTrue(String userId);

    //! active URLs for only admin
    List<UrlShortener> findByActiveTrue();

    // Top 10 most clicked
    List<UrlShortener> findTop10ByActiveTrueOrderByAccessCountDesc();
}