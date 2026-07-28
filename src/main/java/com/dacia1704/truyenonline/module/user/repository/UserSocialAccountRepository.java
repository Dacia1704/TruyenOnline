package com.dacia1704.truyenonline.module.user.repository;

import com.dacia1704.truyenonline.module.authentication.entity.AuthProvider;
import com.dacia1704.truyenonline.module.authentication.entity.UserSocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSocialAccountRepository extends JpaRepository<UserSocialAccount, String> {

    Optional<UserSocialAccount> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);

}