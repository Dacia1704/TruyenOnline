package com.dacia1704.truyenonline.module.authentication.service;

import com.dacia1704.truyenonline.config.GoogleConfig;
import com.dacia1704.truyenonline.module.authentication.dto.GoogleUserInfo;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleTokenVerifier {

    private final GoogleConfig googleConfig;

    public GoogleUserInfo verify(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                                            .setAudience(Collections.singletonList(googleConfig.getClientId()))
                                            .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);

            if (googleIdToken == null) {
                throw new AppException(ErrorCode.GOOGLE_TOKEN_IS_INVALID);
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            return GoogleUserInfo.builder()
                    .googleId(payload.getSubject())
                    .email(payload.getEmail())
                    .name((String) payload.get("name"))
                    .picture((String) payload.get("picture"))
                    .emailVerified(Boolean.TRUE.equals(payload.getEmailVerified()))
                    .build();

        } catch (GeneralSecurityException | IOException e) {
            throw new AppException(ErrorCode.GOOGLE_TOKEN_IS_INVALID);
        }

    }

}