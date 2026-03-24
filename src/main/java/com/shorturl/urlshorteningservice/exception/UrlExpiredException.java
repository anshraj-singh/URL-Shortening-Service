package com.shorturl.urlshorteningservice.exception;

public class UrlExpiredException extends RuntimeException {
    public UrlExpiredException(String shortCode) {
        super("The short URL '" + shortCode + "' has expired and is no longer accessible.");
    }
}