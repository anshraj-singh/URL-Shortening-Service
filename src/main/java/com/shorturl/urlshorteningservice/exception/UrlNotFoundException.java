package com.shorturl.urlshorteningservice.exception;

// ─── Short URL not found ──────────────────────────────────────────────────────
public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String shortCode) {
        super("No URL found for short code: " + shortCode);
    }
}