package com.dacia1704.truyenonline.module.administration.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BanAppealCreateRequest {

    @NotBlank private String moderationActionId;

    @NotBlank private String content;

    @Builder.Default private List<MultipartFile> attachments = new ArrayList<>();
}
