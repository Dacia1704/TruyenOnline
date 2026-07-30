package com.dacia1704.truyenonline.module.authentication.service;

import com.dacia1704.truyenonline.module.authentication.repository.PasswordResetTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PasswordResetTokenService {
    PasswordResetTokenRepository passwordResetTokenRepository;

    public int deleteNotUsablePasswordResetToken() {
        return passwordResetTokenRepository.deleteAllNotUsable(LocalDateTime.now());
    }

}
