package com.shorturl.urlshorteningservice.service;

import com.shorturl.urlshorteningservice.config.AppProperties;
import com.shorturl.urlshorteningservice.dto.CreateUrlRequest;
import com.shorturl.urlshorteningservice.dto.UpdateUrlRequest;
import com.shorturl.urlshorteningservice.dto.UrlResponse;
import com.shorturl.urlshorteningservice.exception.UrlNotFoundException;
import com.shorturl.urlshorteningservice.model.UrlShortener;
import com.shorturl.urlshorteningservice.repository.UrlShortenerRepository;
import com.shorturl.urlshorteningservice.util.UrlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerService {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;

    private final UrlShortenerRepository repository;
    private final AppProperties appProperties;

    // ─── CREATE ──────────────────────────────────────────────────────────────

    public UrlResponse createShortUrl(CreateUrlRequest request) {
        String normalised = normaliseUrl(request.getUrl());
        String shortCode = generateUniqueShortCode();

        UrlShortener entity = UrlShortener.builder()
                .originalUrl(normalised)
                .shortCode(shortCode)
                .accessCount(0)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UrlShortener saved = repository.save(entity);
        log.info("Created short URL: {} → {}", shortCode, normalised);
        return UrlMapper.toResponse(saved, appProperties.getBaseUrl());
    }

    // ─── REDIRECT ─────────────────────────────────────────────────────────────

    public String resolveUrl(String shortCode) {
        UrlShortener entity = findByCode(shortCode);

        if (!entity.isActive()) {
            throw new UrlNotFoundException(shortCode);
        }

        entity.setAccessCount(entity.getAccessCount() + 1);
        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);

        log.info("Redirecting '{}' → {} (hit #{})", shortCode, entity.getOriginalUrl(), entity.getAccessCount());
        return entity.getOriginalUrl();
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    public UrlResponse updateShortUrl(String shortCode, UpdateUrlRequest request) {
        UrlShortener entity = findByCode(shortCode);

        entity.setOriginalUrl(normaliseUrl(request.getNewUrl()));
        entity.setUpdatedAt(LocalDateTime.now());

        UrlShortener updated = repository.save(entity);
        log.info("Updated short URL '{}' → {}", shortCode, request.getNewUrl());
        return UrlMapper.toResponse(updated, appProperties.getBaseUrl());
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    public void deleteShortUrl(String shortCode) {
        UrlShortener entity = findByCode(shortCode);
        entity.setActive(false);
        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);
        log.info("Soft-deleted short URL '{}'", shortCode);
    }

    public void hardDeleteShortUrl(String shortCode) {
        UrlShortener entity = findByCode(shortCode);
        repository.delete(entity);
        log.info("Hard-deleted short URL '{}'", shortCode);
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    public UrlResponse getStats(String shortCode) {
        return UrlMapper.toResponse(findByCode(shortCode), appProperties.getBaseUrl());
    }

    public List<UrlResponse> getAllUrls() {
        return repository.findAll().stream()
                .map(e -> UrlMapper.toResponse(e, appProperties.getBaseUrl()))
                .collect(Collectors.toList());
    }

    public List<UrlResponse> getActiveUrls() {
        return repository.findByActiveTrue().stream()
                .map(e -> UrlMapper.toResponse(e, appProperties.getBaseUrl()))
                .collect(Collectors.toList());
    }

    public List<UrlResponse> getTopUrls() {
        return repository.findTop10ByActiveTrueOrderByAccessCountDesc().stream()
                .map(e -> UrlMapper.toResponse(e, appProperties.getBaseUrl()))
                .collect(Collectors.toList());
    }

    // ─── RESTORE ──────────────────────────────────────────────────────────────

    public UrlResponse reactivateShortUrl(String shortCode) {
        UrlShortener entity = findByCode(shortCode);
        entity.setActive(true);
        entity.setUpdatedAt(LocalDateTime.now());
        return UrlMapper.toResponse(repository.save(entity), appProperties.getBaseUrl());
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private UrlShortener findByCode(String shortCode) {
        return repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));
    }

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

    private String generateUniqueShortCode() {
        Random random = new Random();
        String code;
        int attempts = 0;
        do {
            if (++attempts > 10) {
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