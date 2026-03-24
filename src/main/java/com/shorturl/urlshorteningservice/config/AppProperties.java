package com.shorturl.urlshorteningservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Externalised settings read from application.properties / environment variables.
 * Override BASE_URL via the env var SHORTURL_BASE_URL in production.
 */
@Component
@ConfigurationProperties(prefix = "shorturl")
@Data
public class AppProperties {

    /** Public base URL used when building short links (no trailing slash). */
    private String baseUrl = "http://localhost:8080";
}