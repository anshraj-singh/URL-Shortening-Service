package com.shorturl.urlshorteningservice.controller;


import com.shorturl.urlshorteningservice.model.UrlShortener;
import com.shorturl.urlshorteningservice.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/shorten")
public class UrlShortenerController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    // Endpoint to create a short URL
    @PostMapping
    public ResponseEntity<UrlShortener> createShortUrl(@RequestBody String originalUrl) {
        UrlShortener createdUrl = urlShortenerService.createShortUrl(originalUrl);
        return new ResponseEntity<>(createdUrl, HttpStatus.CREATED);
    }
    // Endpoint to get the original URL from a short code
    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlShortener> getOriginalUrl(@PathVariable String shortCode) {
        UrlShortener urlShortener = urlShortenerService.getOriginalUrl(shortCode);
        if (urlShortener != null) {
            return new ResponseEntity<>(urlShortener, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{shortCode}")
    public ResponseEntity<UrlShortener> updateShortUrl(@PathVariable String shortCode, @RequestBody String newUrl) {
        UrlShortener updatedUrl = urlShortenerService.updateShortUrl(shortCode, newUrl);
        if (updatedUrl != null) {
            return new ResponseEntity<>(updatedUrl, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteShortUrl(@PathVariable String shortCode) {
        boolean isDeleted = urlShortenerService.deleteShortUrl(shortCode);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlShortener> getUrlStatistics(@PathVariable String shortCode) {
        UrlShortener urlShortener = urlShortenerService.getUrlStatistics(shortCode);
        if (urlShortener != null) {
            return new ResponseEntity<>(urlShortener, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
