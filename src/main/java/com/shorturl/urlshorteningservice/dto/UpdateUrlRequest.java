package com.shorturl.urlshorteningservice.dto;

import lombok.Data;

//! Request body for updating the destination of an existing short URL.
@Data
public class UpdateUrlRequest {

    private String newUrl;
}