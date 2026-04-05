package com.shorturl.urlshorteningservice.controller;

import com.shorturl.urlshorteningservice.dto.ApiResponse;
import com.shorturl.urlshorteningservice.dto.BulkDeleteRequest;
import com.shorturl.urlshorteningservice.dto.UrlResponse;
import com.shorturl.urlshorteningservice.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin Controller — /api/v1/admin/**
 * SecurityConfig mein hasRole("ADMIN") set hai.
 *
 * ┌──────────┬─────────────────────────────────────────┬──────────────────────────────────────┐
 * │ Method   │ Path                                    │ Purpose                              │
 * ├──────────┼─────────────────────────────────────────┼──────────────────────────────────────┤
 * │ GET      │ /api/v1/admin/urls?page=0&size=20       │ Saari URLs paginated                 │
 * │ GET      │ /api/v1/admin/urls/active               │ Active URLs                          │
 * │ GET      │ /api/v1/admin/urls/top                  │ Top 10 clicked                       │
 * │ GET      │ /api/v1/admin/urls/flagged              │ Suspicious URLs (spam detection)     │
 * │ GET      │ /api/v1/admin/urls/{code}               │ Kisi bhi URL ka detail               │
 * │ GET      │ /api/v1/admin/urls/{code}/stats         │ Kisi bhi URL ke stats                │
 * │ POST     │ /api/v1/admin/urls/{code}/ban           │ URL ban karo (redirect block)        │
 * │ PATCH    │ /api/v1/admin/urls/{code}/restore       │ URL restore karo                     │
 * │ DELETE   │ /api/v1/admin/urls/{code}               │ Soft delete                          │
 * │ DELETE   │ /api/v1/admin/urls/{code}/hard          │ Permanent delete                     │
 * │ DELETE   │ /api/v1/admin/urls/bulk                 │ Multiple URLs ek saath delete        │
 * └──────────┴─────────────────────────────────────────┴──────────────────────────────────────┘
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UrlShortenerService service;

    @GetMapping("/urls")
    public ResponseEntity<ApiResponse<Page<UrlResponse>>> getAllUrls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<UrlResponse> result = service.getAllUrlsPaginated(page, size);
        return ResponseEntity.ok(ApiResponse.success(result,
                "Page " + page + " — total " + result.getTotalElements() + " URL(s)"));
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

    @GetMapping("/urls/flagged")
    public ResponseEntity<ApiResponse<List<UrlResponse>>> getFlaggedUrls() {
        List<UrlResponse> list = service.getFlaggedUrls();
        return ResponseEntity.ok(ApiResponse.success(list,
                list.isEmpty() ? "No suspicious URLs found" : list.size() + " suspicious URL(s) found"));
    }

    @GetMapping("/urls/{code}")
    public ResponseEntity<ApiResponse<UrlResponse>> getUrl(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(service.getStats(code), "URL fetched successfully"));
    }

    @GetMapping("/urls/{code}/stats")
    public ResponseEntity<ApiResponse<UrlResponse>> getUrlStats(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(service.getStats(code), "Stats fetched successfully"));
    }

    @PostMapping("/urls/{code}/ban")
    public ResponseEntity<ApiResponse<UrlResponse>> banUrl(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(service.banUrl(code), "URL banned successfully"));
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

    @DeleteMapping("/urls/bulk")
    public ResponseEntity<ApiResponse<String>> bulkDelete(
            @RequestBody BulkDeleteRequest request) {

        int count = service.bulkDelete(request.getShortCodes());
        return ResponseEntity.ok(ApiResponse.success(
                count + " URL(s) deactivated", "Bulk delete successful"));
    }
}