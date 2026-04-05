package com.shorturl.urlshorteningservice.dto;

import lombok.Data;
import java.util.List;


@Data
public class BulkDeleteRequest {
    private List<String> shortCodes;
}