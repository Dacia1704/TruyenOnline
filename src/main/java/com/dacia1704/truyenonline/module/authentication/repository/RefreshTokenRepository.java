package com.dacia1704.truyenonline.module.authentication.repository;

import com.dacia1704.truyenonline.module.authentication.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    boolean existsByTokenHash(String tokenHash);
    List<RefreshToken> findAllByUser_Id(String userId);
    long deleteAllByUser_Id(String userId);
    long deleteByExpiredAtBefore(LocalDateTime now);
    Optional<RefreshToken> findByTokenHashAndRevokedAtIsNull(String tokenHash);
    List<RefreshToken> findAllByUser_IdAndRevokedAtIsNull(String userId);
    long deleteByRevokedAtIsNotNull();
    Optional<RefreshToken> findByUser_IdAndDeviceId(String userId, String deviceId);
}