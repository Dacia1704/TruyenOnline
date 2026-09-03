package com.dacia1704.truyenonline.module.media.service;

import com.dacia1704.truyenonline.module.media.dto.response.CloudinaryUploadResult;
import com.dacia1704.truyenonline.module.media.entity.MediaFile;
import com.dacia1704.truyenonline.module.media.respository.MediaFileRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MediaFileService {

    private final MediaFileRepository mediaFileRepository;
    private final CloudinaryService cloudinaryService;

    public void createMediaFile(CloudinaryUploadResult uploadResult) {
        MediaFile mediaFile =
                MediaFile.builder()
                        .publicId(uploadResult.getPublicId())
                        .secureUrl(uploadResult.getSecureUrl())
                        .referenceCount(1)
                        .build();
        mediaFileRepository.save(mediaFile);
    }

    public void increaseReference(String secureUrl) {
        mediaFileRepository
                .findBySecureUrl(secureUrl)
                .ifPresent(
                        mediaFile -> {
                            mediaFile.increment();
                            mediaFileRepository.save(mediaFile);
                        });
    }

    public void decreaseReference(String secureUrl) {
        mediaFileRepository
                .findBySecureUrl(secureUrl)
                .ifPresent(
                        mediaFile -> {
                            mediaFile.decrement();
                            mediaFileRepository.save(mediaFile);
                        });
    }

    public void deleteOrphanImageFileBeforeDays() throws IOException {
        LocalDateTime before = LocalDateTime.now().minusDays(30);
        List<MediaFile> orphanFiles =
                mediaFileRepository.findAllByReferenceCountAndUpdatedAtBefore(0, before);
        for (MediaFile file : orphanFiles) {
            try {
                cloudinaryService.deleteImage(file.getPublicId());
                mediaFileRepository.delete(file);
            } catch (Exception ex) {
                log.error("Cannot delete media {}", file.getId(), ex);
            }
        }
    }
}
