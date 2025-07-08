package com.shorturl.urlshorteningservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "urls")
@Data
public class UrlShortener {

    @Id
    private String id;
    private String url;
    private String shortCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int accessCount;

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getShortCode() {
        return shortCode;
    }

public void setShortCode(String shortCode) {
    this.shortCode = shortCode;
}
public LocalDateTime getCreatedAt() {
    return createdAt;
}
public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
}
public LocalDateTime getUpdatedAt() {
    return updatedAt;
}
public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
}
public int getAccessCount() {
    return accessCount;
}
public void setAccessCount(int accessCount) {
    this.accessCount = accessCount;
}
}
