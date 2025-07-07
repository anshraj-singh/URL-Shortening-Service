package com.shorturl.urlshorteningservice.repository;

import com.shorturl.urlshorteningservice.model.UrlShortener;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrlShortenerRepository extends MongoRepository<UrlShortener, String> {
    UrlShortener findByShortCode(String shortCode);
}
