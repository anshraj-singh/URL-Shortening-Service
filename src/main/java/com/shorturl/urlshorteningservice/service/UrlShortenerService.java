package com.shorturl.urlshorteningservice.service;

import com.shorturl.urlshorteningservice.config.AppProperties;
import com.shorturl.urlshorteningservice.dto.CreateUrlRequest;
import com.shorturl.urlshorteningservice.dto.UpdateUrlRequest;
import com.shorturl.urlshorteningservice.dto.UrlResponse;
import com.shorturl.urlshorteningservice.exception.UrlExpiredException;
import com.shorturl.urlshorteningservice.exception.UrlNotFoundException;
import com.shorturl.urlshorteningservice.model.UrlShortener;
import com.shorturl.urlshorteningservice.repository.UrlShortenerRepository;
import com.shorturl.urlshorteningservice.util.UrlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Core business logic for the URL Shortener service.
 *
 * Responsibilities:
 *  - Generate collision-free short codes
 *  - Validate & normalise URLs
 *  - Track access counts & last-accessed timestamps
 *  - Handle expiry (TTL) checks
 *  - Provide statistics & bulk-query endpoints
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerService {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UrlShortenerRepository repository;
    private final AppProperties appProperties;

    // ─── CREATE ──────────────────────────────────────────────────────────────

    /**
     * Creates a new short URL with a randomly generated 6-char code.
     */
    public UrlResponse createShortUrl(CreateUrlRequest request) {
        String normalised = normaliseUrl(request.getUrl());
        String shortCode = generateUniqueShortCode();

        UrlShortener entity = UrlShortener.builder()
                .originalUrl(normalised)
                .shortCode(shortCode)
                .title(request.getTitle())
                .createdBy(request.getCreatedBy())
                .expiresAt(request.getExpiresAt())
                .tags(request.getTags() != null ? request.getTags() : List.of())
                .accessCount(0)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UrlShortener saved = repository.save(entity);
        log.info("Created short URL: {} → {}", shortCode, normalised);
        return UrlMapper.toResponse(saved, appProperties.getBaseUrl());
    }

    // ─── RESOLVE / REDIRECT ───────────────────────────────────────────────────

    /**
     * Resolves a shortCode to the original URL.
     * Increments access counter and checks expiry.
     */
    public String resolveUrl(String code) {
        UrlShortener entity = findByCode(code);

        if (!entity.isActive()) {
            throw new UrlNotFoundException(code);
        }

        if (entity.getExpiresAt() != null && entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException(code);
        }

        // Track usage
        entity.setAccessCount(entity.getAccessCount() + 1);
        entity.setLastAccessedAt(LocalDateTime.now().format(FORMATTER));
        repository.save(entity);

        log.info("Resolved '{}' → {} (hit #{})", code, entity.getOriginalUrl(), entity.getAccessCount());
        return entity.getOriginalUrl();
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    /**
     * Updates the target URL and optional metadata of an existing short link.
     */
    public UrlResponse updateShortUrl(String shortCode, UpdateUrlRequest request) {
        UrlShortener entity = findByCode(shortCode);

        entity.setOriginalUrl(normaliseUrl(request.getNewUrl()));
        entity.setUpdatedAt(LocalDateTime.now());

        if (request.getTitle() != null) entity.setTitle(request.getTitle());
        if (request.getExpiresAt() != null) entity.setExpiresAt(request.getExpiresAt());
        if (request.getTags() != null) entity.setTags(request.getTags());

        UrlShortener updated = repository.save(entity);
        log.info("Updated short URL '{}' → {}", shortCode, request.getNewUrl());
        return UrlMapper.toResponse(updated, appProperties.getBaseUrl());
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    /**
     * Soft-deletes a short URL (sets active=false). The record is kept for analytics.
     */
    public void deleteShortUrl(String shortCode) {
        UrlShortener entity = findByCode(shortCode);
        entity.setActive(false);
        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);
        log.info("Soft-deleted short URL '{}'", shortCode);
    }

    /**
     * Permanently removes a short URL record from the database.
     */
    public void hardDeleteShortUrl(String shortCode) {
        UrlShortener entity = findByCode(shortCode);
        repository.delete(entity);
        log.info("Hard-deleted short URL '{}'", shortCode);
    }

    // ─── STATS ────────────────────────────────────────────────────────────────

    /** Returns full statistics object for a short code. */
    public UrlResponse getStats(String shortCode) {
        UrlShortener entity = findByCode(shortCode);
        return UrlMapper.toResponse(entity, appProperties.getBaseUrl());
    }

    /** Returns all URLs (active + inactive). */
    public List<UrlResponse> getAllUrls() {
        return repository.findAll().stream()
                .map(e -> UrlMapper.toResponse(e, appProperties.getBaseUrl()))
                .collect(Collectors.toList());
    }

    /** Returns only active URLs. */
    public List<UrlResponse> getActiveUrls() {
        return repository.findByActiveTrue().stream()
                .map(e -> UrlMapper.toResponse(e, appProperties.getBaseUrl()))
                .collect(Collectors.toList());
    }

    /** Returns top 10 most-accessed active URLs. */
    public List<UrlResponse> getTopUrls() {
        return repository.findTop10ByActiveTrueOrderByAccessCountDesc().stream()
                .map(e -> UrlMapper.toResponse(e, appProperties.getBaseUrl()))
                .collect(Collectors.toList());
    }

    /** Returns all URLs created by a specific user/client. */
    public List<UrlResponse> getUrlsByCreator(String createdBy) {
        return repository.findByCreatedBy(createdBy).stream()
                .map(e -> UrlMapper.toResponse(e, appProperties.getBaseUrl()))
                .collect(Collectors.toList());
    }

    /** Returns all URLs tagged with a given tag. */
    public List<UrlResponse> getUrlsByTag(String tag) {
        return repository.findByTag(tag).stream()
                .map(e -> UrlMapper.toResponse(e, appProperties.getBaseUrl()))
                .collect(Collectors.toList());
    }

    // ─── REACTIVATE / EXPIRE ─────────────────────────────────────────────────

    /** Reactivates a previously soft-deleted URL. */
    public UrlResponse reactivateShortUrl(String shortCode) {
        UrlShortener entity = findByCode(shortCode);
        entity.setActive(true);
        entity.setUpdatedAt(LocalDateTime.now());
        return UrlMapper.toResponse(repository.save(entity), appProperties.getBaseUrl());
    }

    /** Manually expires a URL immediately. */
    public UrlResponse expireShortUrl(String shortCode) {
        UrlShortener entity = findByCode(shortCode);
        entity.setExpiresAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return UrlMapper.toResponse(repository.save(entity), appProperties.getBaseUrl());
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private UrlShortener findByCode(String shortCode) {
        return repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));
    }

    /**
     * Ensures the URL has a protocol prefix.
     * "google.com" → "https://google.com"
     */
    private String normaliseUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL must not be blank");
        }
        url = url.trim();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        return url;
    }

    /**
     * Generates a random 6-character alphanumeric code that is guaranteed
     * not to collide with any existing short code or alias.
     */
    private String generateUniqueShortCode() {
        Random random = new Random();
        String code;
        int attempts = 0;
        do {
            if (++attempts > 10) {
                // Very unlikely but defensive: increase length on repeated collisions
                throw new RuntimeException("Could not generate a unique short code. Please try again.");
            }
            StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
            for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
                sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            code = sb.toString();
        } while (repository.existsByShortCode(code));
        return code;
    }
}