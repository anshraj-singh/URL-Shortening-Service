package com.shorturl.urlshorteningservice.service;

import com.shorturl.urlshorteningservice.model.UrlShortener;
import com.shorturl.urlshorteningservice.repository.UrlShortenerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UrlShortenerService {

    @Autowired
    private UrlShortenerRepository urlShortenerRepository;

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;

    public UrlShortener createShortUrl(String originalUrl) {
        // Fix missing protocol
        if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
            originalUrl = "https://" + originalUrl;
        }

        UrlShortener urlShortener = new UrlShortener();
        urlShortener.setUrl(originalUrl);
        urlShortener.setShortCode(generateShortCode());
        urlShortener.setCreatedAt(LocalDateTime.now());
        urlShortener.setUpdatedAt(LocalDateTime.now());
        urlShortener.setAccessCount(0);
        return urlShortenerRepository.save(urlShortener);
    }

    public String getOriginalUrl(String shortCode) {
        UrlShortener url = urlShortenerRepository.findByShortCode(shortCode);
        if (url == null) return null;

        String originalUrl = url.getUrl();
        if (originalUrl == null || !(originalUrl.startsWith("http://") || originalUrl.startsWith("https://"))) {
            System.err.println("Invalid original URL: " + originalUrl); // debug log
            return null;
        }

        url.setAccessCount(url.getAccessCount() + 1);
        urlShortenerRepository.save(url);
        return originalUrl;
    }



    public UrlShortener updateShortUrl(String shortCode, String newUrl) {
        UrlShortener urlShortener = urlShortenerRepository.findByShortCode(shortCode);
        if (urlShortener != null) {
            urlShortener.setUrl(newUrl); // Update the original URL
            urlShortener.setUpdatedAt(LocalDateTime.now()); // Update the timestamp
            return urlShortenerRepository.save(urlShortener); // Save the updated URL
        }
        return null; // Return null if not found
    }

    public boolean deleteShortUrl(String shortCode) {
        UrlShortener urlShortener = urlShortenerRepository.findByShortCode(shortCode);
        if (urlShortener != null) {
            urlShortenerRepository.delete(urlShortener); // Delete the URL
            return true; // Return true if deleted
        }
        return false; // Return false if not found
    }

    public UrlShortener getUrlStatistics(String shortCode) {
        return urlShortenerRepository.findByShortCode(shortCode); // Retrieve statistics for the short URL
    }

    private String generateShortCode() {
        Random random = new Random();
        StringBuilder shortCode = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            shortCode.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length()))); // Generate a random short code
        }
        return shortCode.toString(); // Return the generated short code
    }
}
