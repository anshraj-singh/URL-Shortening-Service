package com.shorturl.urlshorteningservice.controller;

import com.shorturl.urlshorteningservice.dto.ApiResponse;
import com.shorturl.urlshorteningservice.dto.UrlResponse;
import com.shorturl.urlshorteningservice.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    @Autowired
    private UrlShortenerService service;

    @GetMapping("/urls")
    public ResponseEntity<ApiResponse<List<UrlResponse>>> getAllUrls() {
        List<UrlResponse> list = service.getAllUrls();
        return ResponseEntity.ok(ApiResponse.success(list, "Total " + list.size() + " URL(s) found"));
    }

    @GetMapping("/urls/active")
    public ResponseEntity<ApiResponse<List<UrlResponse>>> getActiveUrls() {
        List<UrlResponse> list = service.getActiveUrls();
        return ResponseEntity.ok(ApiResponse.success(list, "Total " + list.size() + " active URL(s)"));
    }

    @GetMapping("/urls/top")
    public ResponseEntity<ApiResponse<List<UrlResponse>>> getTopUrls() {
        return ResponseEntity.ok(ApiResponse.success(service.getTopUrls(), "Top URLs fetched"));
    }

    @GetMapping("/urls/{code}")
    public ResponseEntity<ApiResponse<UrlResponse>> getUrl(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(service.getStats(code), "URL fetched successfully"));
    }

    @GetMapping("/urls/{code}/stats")
    public ResponseEntity<ApiResponse<UrlResponse>> getUrlStats(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(service.getStats(code), "Stats fetched successfully"));
    }

    @PatchMapping("/urls/{code}/restore")
    public ResponseEntity<ApiResponse<UrlResponse>> restoreUrl(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(service.reactivateShortUrl(code), "URL restored successfully"));
    }

    @DeleteMapping("/urls/{code}")
    public ResponseEntity<ApiResponse<Void>> softDeleteUrl(@PathVariable String code) {
        service.deleteShortUrl(code);
        return ResponseEntity.ok(ApiResponse.success(null, "URL deactivated by admin"));
    }

    @DeleteMapping("/urls/{code}/hard")
    public ResponseEntity<ApiResponse<Void>> hardDeleteUrl(@PathVariable String code) {
        service.hardDeleteShortUrl(code);
        return ResponseEntity.ok(ApiResponse.success(null, "URL permanently deleted by admin"));
    }
}