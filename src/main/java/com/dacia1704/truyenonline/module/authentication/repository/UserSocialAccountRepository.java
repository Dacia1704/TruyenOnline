package com.dacia1704.truyenonline.module.authentication.repository;

import com.dacia1704.truyenonline.module.authentication.entity.AuthProvider;
import com.dacia1704.truyenonline.module.authentication.entity.UserSocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserSocialAccountRepository extends JpaRepository<UserSocialAccount, String> {

    Optional<UserSocialAccount> findByProviderAndProviderUserId(
            AuthProvider provider,
            String providerUserId
    );

    List<UserSocialAccount> findAllByUser_Id(String userId);

    Optional<UserSocialAccount> findByUser_IdAndProvider(
            String userId,
            AuthProvider provider
    );

    boolean existsByUser_IdAndProvider(
            String userId,
            AuthProvider provider
    );

    long countByUser_Id(String userId);

}