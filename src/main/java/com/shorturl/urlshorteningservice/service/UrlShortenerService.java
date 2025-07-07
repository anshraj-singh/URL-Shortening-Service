package com.shorturl.urlshorteningservice.service;

import com.shorturl.urlshorteningservice.repository.UrlShortenerRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class UrlShortenerService {

    @Autowired
    private UrlShortenerRepository urlShortenerRepository;
}
