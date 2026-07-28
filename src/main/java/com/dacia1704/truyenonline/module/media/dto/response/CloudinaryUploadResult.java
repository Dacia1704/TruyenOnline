package com.dacia1704.truyenonline.module.media.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CloudinaryUploadResult {
    private String secureUrl; // Map vào cột image_url
    private String publicId; // Map vào cột cloudinary_id
    private Integer width; // Map vào cột width
    private Integer height; // Map vào cột height
}
