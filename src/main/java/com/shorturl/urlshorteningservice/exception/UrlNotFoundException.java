package com.shorturl.urlshorteningservice.exception;

//! when short url will not found
public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String shortCode) {
        super("No URL found for short code: " + shortCode);
    }
}