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

    //! Duplicate URL check
    Optional<UrlShortener> findByOriginalUrlAndActiveTrue(String originalUrl);

    List<UrlShortener> findByActiveTrue();

    List<UrlShortener> findTop10ByActiveTrueOrderByAccessCountDesc();
}