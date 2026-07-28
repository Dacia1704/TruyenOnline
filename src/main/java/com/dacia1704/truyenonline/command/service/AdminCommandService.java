package com.dacia1704.truyenonline.command.service;

import com.dacia1704.truyenonline.module.user.entity.Role;
import com.dacia1704.truyenonline.module.user.entity.RoleName;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.repository.RoleRepository;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCommandService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Transactional
    public void createAdmin(
            String username,
            String email,
            String password
    ) {

        if (userRepository.existsByUsername(username)) throw new AppException(ErrorCode.USER_EXISTED);
        if (userRepository.existsByEmail(email)) throw new AppException(ErrorCode.USER_EXISTED);

        Role adminRole = roleRepository.findByName(RoleName.ADMIN.toString()).orElseThrow(() -> new IllegalStateException("ADMIN role not found."));

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .build();

        user.getRoles().add(adminRole);

        userRepository.save(user);

        log.info("======================================");
        log.info("Admin account created successfully.");
        log.info("Username : {}", username);
        log.info("Email    : {}", email);
        log.info("======================================");
    }

}