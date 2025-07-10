package com.shorturl.urlshorteningservice.controller;

import com.shorturl.urlshorteningservice.model.UrlRequest;
import com.shorturl.urlshorteningservice.model.UrlShortener;
import com.shorturl.urlshorteningservice.service.UrlShortenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/shorUrl")
public class UrlShortenerController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);

    @PostMapping
    public ResponseEntity<UrlShortener> createShortUrl(@RequestBody UrlRequest request) {
        String originalUrl = request.getUrl();
        try {
            UrlShortener createdUrl = urlShortenerService.createShortUrl(originalUrl);
            return new ResponseEntity<>(createdUrl, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating short URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        logger.info("Fetching original URL for short code: {}", shortCode);
        try {
            String originalUrl = urlShortenerService.getOriginalUrl(shortCode);
            if (originalUrl != null) {
                logger.info("Redirecting to original URL: {}", originalUrl);
                return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
            } else {
                logger.warn("Short code not found: {}", shortCode);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error fetching original URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{shortCode}")
    public ResponseEntity<UrlShortener> updateShortUrl(@PathVariable String shortCode, @RequestBody String newUrl) {
        try {
            UrlShortener updatedUrl = urlShortenerService.updateShortUrl(shortCode, newUrl);
            if (updatedUrl != null) {
                return new ResponseEntity<>(updatedUrl, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error updating short URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteShortUrl(@PathVariable String shortCode) {
        try {
            boolean isDeleted = urlShortenerService.deleteShortUrl(shortCode);
            if (isDeleted) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error deleting short URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlShortener> getUrlStatistics(@PathVariable String shortCode) {
        try {
            UrlShortener urlShortener = urlShortenerService.getUrlStatistics(shortCode);
            if (urlShortener != null) {
                return new ResponseEntity<>(urlShortener, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error fetching URL statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
