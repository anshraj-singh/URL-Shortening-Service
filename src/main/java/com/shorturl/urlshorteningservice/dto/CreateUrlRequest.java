package com.shorturl.urlshorteningservice.dto;

import lombok.Data;

//! Request body for creating a new short URL Only the original URL is required.
@Data
public class CreateUrlRequest {

    private String url;
}