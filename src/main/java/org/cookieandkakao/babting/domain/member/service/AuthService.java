package org.cookieandkakao.babting.domain.member.service;

import org.cookieandkakao.babting.domain.member.properties.KakaoClientProperties;
import org.cookieandkakao.babting.domain.member.properties.KakaoProviderProperties;
import org.cookieandkakao.babting.domain.member.util.AuthorizationUriBuilder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final KakaoClientProperties kakaoClientProperties;
    private final KakaoProviderProperties kakaoProviderProperties;

    public AuthService(KakaoClientProperties kakaoClientProperties,
        KakaoProviderProperties kakaoProviderProperties) {
        this.kakaoClientProperties = kakaoClientProperties;
        this.kakaoProviderProperties = kakaoProviderProperties;
    }

    public String getAuthUrl() {
        return new AuthorizationUriBuilder()
            .clientProperties(kakaoClientProperties)
            .providerProperties(kakaoProviderProperties)
            .build();
    }
}
