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


@RestController
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerController {

    private final UrlShortenerService service;

    @PostMapping("/api/v1/urls")
    public ResponseEntity<ApiResponse<UrlResponse>> createShortUrl(
            @RequestBody CreateUrlRequest request) {

        UrlResponse response = service.createShortUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Short URL created successfully"));
    }

    @GetMapping("/r/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String originalUrl = service.resolveUrl(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @GetMapping("/api/v1/urls/{code}")
    public ResponseEntity<ApiResponse<UrlResponse>> getUrl(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(service.getStats(code), "URL fetched successfully"));
    }

    @GetMapping("/api/v1/urls/{code}/stats")
    public ResponseEntity<ApiResponse<UrlResponse>> getStats(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(service.getStats(code), "Stats fetched successfully"));
    }

    @GetMapping("/api/v1/urls")
    public ResponseEntity<ApiResponse<List<UrlResponse>>> getAllUrls() {
        List<UrlResponse> list = service.getAllUrls();
        return ResponseEntity.ok(ApiResponse.success(list, "Fetched " + list.size() + " URL(s)"));
    }

    @GetMapping("/api/v1/urls/active")
    public ResponseEntity<ApiResponse<List<UrlResponse>>> getActiveUrls() {
        List<UrlResponse> list = service.getActiveUrls();
        return ResponseEntity.ok(ApiResponse.success(list, "Fetched " + list.size() + " active URL(s)"));
    }

    @GetMapping("/api/v1/urls/top")
    public ResponseEntity<ApiResponse<List<UrlResponse>>> getTopUrls() {
        return ResponseEntity.ok(ApiResponse.success(service.getTopUrls(), "Top URLs fetched"));
    }

    @PutMapping("/api/v1/urls/{code}")
    public ResponseEntity<ApiResponse<UrlResponse>> updateUrl(
            @PathVariable String code,
            @RequestBody UpdateUrlRequest request) {

        return ResponseEntity.ok(ApiResponse.success(service.updateShortUrl(code, request), "URL updated successfully"));
    }


    @PatchMapping("/api/v1/urls/{code}/restore")
    public ResponseEntity<ApiResponse<UrlResponse>> restoreUrl(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(service.reactivateShortUrl(code), "URL restored successfully"));
    }


    @DeleteMapping("/api/v1/urls/{code}")
    public ResponseEntity<ApiResponse<Void>> deleteUrl(@PathVariable String code) {
        service.deleteShortUrl(code);
        return ResponseEntity.ok(ApiResponse.success(null, "URL deactivated successfully"));
    }

    @DeleteMapping("/api/v1/urls/{code}/hard")
    public ResponseEntity<ApiResponse<Void>> hardDeleteUrl(@PathVariable String code) {
        service.hardDeleteShortUrl(code);
        return ResponseEntity.ok(ApiResponse.success(null, "URL permanently deleted"));
    }
}