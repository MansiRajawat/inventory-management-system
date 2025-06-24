package com.project.inventory.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.MediaType;

@Builder
@Getter
public class FileResponse {
    private byte[] data;
    private String fileName;
    private MediaType mediaType;
}
