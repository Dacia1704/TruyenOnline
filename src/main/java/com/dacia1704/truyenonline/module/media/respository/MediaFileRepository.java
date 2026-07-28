package com.dacia1704.truyenonline.module.media.respository;

import com.dacia1704.truyenonline.module.media.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MediaFileRepository extends JpaRepository<MediaFile, String> {
    Optional<MediaFile> findByPublicId(String publicId);
    Optional<MediaFile> findBySecureUrl(String secureUrl);
    boolean existsByPublicId(String publicId);
    boolean existsBySecureUrl(String secureUrl);
    List<MediaFile> findAllByReferenceCount(Integer referenceCount);

    List<MediaFile> findAllByReferenceCountAndUpdatedAtBefore(
            Integer referenceCount,
            LocalDateTime updatedAt
    );
}