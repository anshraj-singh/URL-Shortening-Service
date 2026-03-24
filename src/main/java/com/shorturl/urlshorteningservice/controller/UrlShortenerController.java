package com.shorturl.urlshorteningservice.controller;

import com.shorturl.urlshorteningservice.dto.ApiResponse;
import com.shorturl.urlshorteningservice.dto.CreateUrlRequest;
import com.shorturl.urlshorteningservice.dto.UpdateUrlRequest;
import com.shorturl.urlshorteningservice.dto.UrlResponse;
import com.shorturl.urlshorteningservice.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST controller for the URL Shortener service.
 *
 * Base path: /api/v1/urls
 *
 * ┌─────────────────────────────────────────────────────────────────────┐
 * │  ENDPOINT SUMMARY                                                   │
 * ├──────────────┬──────────────────────────────┬──────────────────────┤
 * │  Method      │  Path                        │  Purpose             │
 * ├──────────────┼──────────────────────────────┼──────────────────────┤
 * │  POST        │  /api/v1/urls                │  Create short URL    │
 * │  GET         │  /r/{code}                   │  Redirect (browser)  │
 * │  GET         │  /api/v1/urls                │  List all URLs       │
 * │  GET         │  /api/v1/urls/active         │  List active URLs    │
 * │  GET         │  /api/v1/urls/top            │  Top 10 clicked      │
 * │  GET         │  /api/v1/urls/{code}         │  Get single URL info │
 * │  GET         │  /api/v1/urls/{code}/stats   │  Stats for a URL     │
 * │  GET         │  /api/v1/urls/creator/{name} │  URLs by creator     │
 * │  GET         │  /api/v1/urls/tag/{tag}      │  URLs by tag         │
 * │  PUT         │  /api/v1/urls/{code}         │  Update target URL   │
 * │  PATCH       │  /api/v1/urls/{code}/expire  │  Expire immediately  │
 * │  PATCH       │  /api/v1/urls/{code}/restore │  Reactivate URL      │
 * │  DELETE      │  /api/v1/urls/{code}         │  Soft-delete URL     │
 * │  DELETE      │  /api/v1/urls/{code}/hard    │  Permanent delete    │
 * └──────────────┴──────────────────────────────┴──────────────────────┘
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerController {

    private final UrlShortenerService service;

    // ─── CREATE ──────────────────────────────────────────────────────────────

    /**
     * POST /api/v1/urls
     * Creates a new short URL.
     *
     * Body example:
     * {
     *   "url": "https://www.example.com/very/long/path?q=1",
     *   "title": "Example page",         // optional
     *   "createdBy": "user123",          // optional
     *   "expiresAt": "2025-12-31T23:59:59", // optional
     *   "tags": ["marketing", "q4"]      // optional
     * }
     */
    @PostMapping("/api/v1/urls")
    public ResponseEntity<ApiResponse<UrlResponse>> createShortUrl(
            @RequestBody CreateUrlRequest request) {

        UrlResponse response = service.createShortUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Short URL created successfully"));
    }

    // ─── REDIRECT ────────────────────────────────────────────────────────────

    /**
     * GET /r/{code}
     * Browser-facing redirect endpoint.
     * Resolves shortCode and returns HTTP 302 to the original URL.
     *
     * This path is kept intentionally short so the final short URL looks like:
     *   http://localhost:8080/r/abc123
     */
    @GetMapping("/r/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String originalUrl = service.resolveUrl(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    // ─── READ — individual ───────────────────────────────────────────────────

    /**
     * GET /api/v1/urls/{code}
     * Returns full metadata for a short URL without incrementing the counter.
     */
    @GetMapping("/api/v1/urls/{code}")
    public ResponseEntity<ApiResponse<UrlResponse>> getUrl(@PathVariable String code) {
        UrlResponse response = service.getStats(code);
        return ResponseEntity.ok(ApiResponse.success(response, "URL fetched successfully"));
    }

    /**
     * GET /api/v1/urls/{code}/stats
     * Explicit statistics endpoint (same data as GET /{code}, kept for clarity).
     */
    @GetMapping("/api/v1/urls/{code}/stats")
    public ResponseEntity<ApiResponse<UrlResponse>> getStats(@PathVariable String code) {
        UrlResponse response = service.getStats(code);
        return ResponseEntity.ok(ApiResponse.success(response, "Stats fetched successfully"));
    }

    // ─── READ — collections ──────────────────────────────────────────────────

    /**
     * GET /api/v1/urls
     * Returns every URL record (active and soft-deleted).
     */
    @GetMapping("/api/v1/urls")
    public ResponseEntity<ApiResponse<List<UrlResponse>>> getAllUrls() {
        List<UrlResponse> list = service.getAllUrls();
        return ResponseEntity.ok(ApiResponse.success(list, "Fetched " + list.size() + " URL(s)"));
    }

    /**
     * GET /api/v1/urls/active
     * Returns only URLs that are currently active (not soft-deleted).
     */
    @GetMapping("/api/v1/urls/active")
    public ResponseEntity<ApiResponse<List<UrlResponse>>> getActiveUrls() {
        List<UrlResponse> list = service.getActiveUrls();
        return ResponseEntity.ok(ApiResponse.success(list, "Fetched " + list.size() + " active URL(s)"));
    }

    /**
     * GET /api/v1/urls/top
     * Returns the 10 most-clicked active short URLs.
     */
    @GetMapping("/api/v1/urls/top")
    public ResponseEntity<ApiResponse<List<UrlResponse>>> getTopUrls() {
        List<UrlResponse> list = service.getTopUrls();
        return ResponseEntity.ok(ApiResponse.success(list, "Top URLs fetched"));
    }

    /**
     * GET /api/v1/urls/creator/{createdBy}
     * Returns all URLs created by a specific user or client identifier.
     */
    @GetMapping("/api/v1/urls/creator/{createdBy}")
    public ResponseEntity<ApiResponse<List<UrlResponse>>> getByCreator(
            @PathVariable String createdBy) {
        List<UrlResponse> list = service.getUrlsByCreator(createdBy);
        return ResponseEntity.ok(ApiResponse.success(list, "URLs by creator '" + createdBy + "'"));
    }

    /**
     * GET /api/v1/urls/tag/{tag}
     * Returns all URLs that have the specified tag.
     */
    @GetMapping("/api/v1/urls/tag/{tag}")
    public ResponseEntity<ApiResponse<List<UrlResponse>>> getByTag(@PathVariable String tag) {
        List<UrlResponse> list = service.getUrlsByTag(tag);
        return ResponseEntity.ok(ApiResponse.success(list, "URLs with tag '" + tag + "'"));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    /**
     * PUT /api/v1/urls/{code}
     * Updates the target URL and/or metadata of an existing short link.
     *
     * Body example:
     * {
     *   "newUrl": "https://www.new-destination.com",
     *   "title": "Updated title",
     *   "expiresAt": "2026-06-01T00:00:00",
     *   "tags": ["updated"]
     * }
     */
    @PutMapping("/api/v1/urls/{code}")
    public ResponseEntity<ApiResponse<UrlResponse>> updateUrl(
            @PathVariable String code,
            @RequestBody UpdateUrlRequest request) {

        UrlResponse response = service.updateShortUrl(code, request);
        return ResponseEntity.ok(ApiResponse.success(response, "URL updated successfully"));
    }

    // ─── PATCH — state changes ────────────────────────────────────────────────

    /**
     * PATCH /api/v1/urls/{code}/expire
     * Immediately expires a short URL (sets expiresAt to now).
     * Any redirect attempt after this returns 410 Gone.
     */
    @PatchMapping("/api/v1/urls/{code}/expire")
    public ResponseEntity<ApiResponse<UrlResponse>> expireUrl(@PathVariable String code) {
        UrlResponse response = service.expireShortUrl(code);
        return ResponseEntity.ok(ApiResponse.success(response, "URL expired successfully"));
    }

    /**
     * PATCH /api/v1/urls/{code}/restore
     * Reactivates a previously soft-deleted URL.
     */
    @PatchMapping("/api/v1/urls/{code}/restore")
    public ResponseEntity<ApiResponse<UrlResponse>> restoreUrl(@PathVariable String code) {
        UrlResponse response = service.reactivateShortUrl(code);
        return ResponseEntity.ok(ApiResponse.success(response, "URL restored successfully"));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    /**
     * DELETE /api/v1/urls/{code}
     * Soft-deletes the URL (sets active=false). Record is preserved for analytics.
     */
    @DeleteMapping("/api/v1/urls/{code}")
    public ResponseEntity<ApiResponse<Void>> deleteUrl(@PathVariable String code) {
        service.deleteShortUrl(code);
        return ResponseEntity.ok(ApiResponse.success(null, "URL deactivated successfully"));
    }

    /**
     * DELETE /api/v1/urls/{code}/hard
     * Permanently removes the record from MongoDB. Use with caution.
     */
    @DeleteMapping("/api/v1/urls/{code}/hard")
    public ResponseEntity<ApiResponse<Void>> hardDeleteUrl(@PathVariable String code) {
        service.hardDeleteShortUrl(code);
        return ResponseEntity.ok(ApiResponse.success(null, "URL permanently deleted"));
    }
}