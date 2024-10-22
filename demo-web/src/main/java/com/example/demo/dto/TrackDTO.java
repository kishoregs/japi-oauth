package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrackDTO {
    private String id;
    private String name;
    private String artists;
    private String album;
    private String albumImageUrl;
    private int durationMs;
    private String previewUrl;
}
