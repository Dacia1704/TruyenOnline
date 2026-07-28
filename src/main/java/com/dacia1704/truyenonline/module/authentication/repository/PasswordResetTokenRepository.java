package com.dacia1704.truyenonline.module.authentication.repository;

import com.dacia1704.truyenonline.module.authentication.entity.PasswordResetToken;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    void deleteAllByUser_Id(String userId);
    void deleteByExpiredAtBefore(LocalDateTime now);
    @Modifying
    @Query("""
        DELETE FROM PasswordResetToken prt
        WHERE prt.usedAt IS NOT NULL
           OR prt.expiredAt < :now
    """)
    int deleteAllNotUsable(@Param("now") LocalDateTime now);
}