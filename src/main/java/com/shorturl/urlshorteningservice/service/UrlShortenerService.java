package com.shorturl.urlshorteningservice.service;

import com.shorturl.urlshorteningservice.config.AppProperties;
import com.shorturl.urlshorteningservice.dto.CreateUrlRequest;
import com.shorturl.urlshorteningservice.dto.UpdateUrlRequest;
import com.shorturl.urlshorteningservice.dto.UrlResponse;
import com.shorturl.urlshorteningservice.model.UrlShortener;
import com.shorturl.urlshorteningservice.repository.UrlShortenerRepository;
import com.shorturl.urlshorteningservice.util.UrlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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


    public UrlResponse createShortUrl(CreateUrlRequest request) {

        String normalised = normaliseUrl(request.getUrl());

        //! to check URL format valid or not
        validateUrlFormat(normalised);

        //! Duplicate check
        Optional<UrlShortener> existing = repository.findByOriginalUrlAndActiveTrue(normalised);
        if (existing.isPresent()) {
            log.info("Duplicate URL found — returning existing short code: {}", existing.get().getShortCode());
            return UrlMapper.toResponse(existing.get(), appProperties.getBaseUrl());
        }

        //! Unique 6-char short code generate
        String shortCode = generateUniqueShortCode();

        //! make MongoDB document and save
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

        //! Response DTO return
        return UrlMapper.toResponse(saved, appProperties.getBaseUrl());
    }


    public String resolveUrl(String shortCode) {
        UrlShortener entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("Short code not found: {}", shortCode);
                    return new RuntimeException("Short URL not found: " + shortCode);
                });

        if (!entity.isActive()) {
            log.warn("Short code is deactivated: {}", shortCode);
            throw new RuntimeException("Short URL is no longer active: " + shortCode);
        }

        entity.setAccessCount(entity.getAccessCount() + 1);
        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);

        log.info("Redirecting '{}' → {} (hit #{})", shortCode, entity.getOriginalUrl(), entity.getAccessCount());
        return entity.getOriginalUrl();
    }

    public UrlResponse previewUrl(String shortCode) {
        UrlShortener entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("Preview failed — short code not found: {}", shortCode);
                    return new RuntimeException("Short URL not found: " + shortCode);
                });

        if (!entity.isActive()) {
            log.warn("Preview failed — short code is deactivated: {}", shortCode);
            throw new RuntimeException("Short URL is no longer active: " + shortCode);
        }

        log.info("Preview requested for '{}'", shortCode);
        return UrlMapper.toResponse(entity, appProperties.getBaseUrl());
    }

    public UrlResponse updateShortUrl(String shortCode, UpdateUrlRequest request) {
        UrlShortener entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("Update failed — short code not found: {}", shortCode);
                    return new RuntimeException("Short URL not found: " + shortCode);
                });
        String normalised = normaliseUrl(request.getNewUrl());
        validateUrlFormat(normalised);

        entity.setOriginalUrl(normalised);
        entity.setUpdatedAt(LocalDateTime.now());

        UrlShortener updated = repository.save(entity);
        log.info("Updated '{}' → {}", shortCode, request.getNewUrl());
        return UrlMapper.toResponse(updated, appProperties.getBaseUrl());
    }

    public void deleteShortUrl(String shortCode) {
        //! find Record
        UrlShortener entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("Delete failed — short code not found: {}", shortCode);
                    return new RuntimeException("Short URL not found: " + shortCode);
                });
        entity.setActive(false);
        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);
        log.info("Soft-deleted short URL '{}'", shortCode);
    }

    public void hardDeleteShortUrl(String shortCode) {
        UrlShortener entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("Hard delete failed — short code not found: {}", shortCode);
                    return new RuntimeException("Short URL not found: " + shortCode);
                });

        // permanently remove from mongodb
        repository.delete(entity);
        log.info("Hard-deleted short URL '{}'", shortCode);
    }

    public UrlResponse getStats(String shortCode) {
        UrlShortener entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("Stats not found for short code: {}", shortCode);
                    return new RuntimeException("Short URL not found: " + shortCode);
                });

        return UrlMapper.toResponse(entity, appProperties.getBaseUrl());
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

    public UrlResponse reactivateShortUrl(String shortCode) {
        UrlShortener entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("Restore failed — short code not found: {}", shortCode);
                    return new RuntimeException("Short URL not found: " + shortCode);
                });
        entity.setActive(true);
        entity.setUpdatedAt(LocalDateTime.now());
        return UrlMapper.toResponse(repository.save(entity), appProperties.getBaseUrl());
    }
    private String normaliseUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new RuntimeException("URL must not be blank");
        }
        url = url.trim();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        return url;
    }

    private void validateUrlFormat(String url) {
        try {
            new URL(url).toURI();
            log.debug("URL format valid: {}", url);
        } catch (Exception e) {
            log.warn("Invalid URL format: {}", url);
            throw new RuntimeException("Invalid URL format: " + url);
        }
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