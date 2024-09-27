package org.cookieandkakao.babting.domain.member.properties;

import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth2.client.kakao")
public record KakaoClientProperties(String clientId, String clientSecret, String redirectUri,
                                    Set<String> scope) {

    @Override
    public String clientId() {
        return clientId;
    }

    @Override
    public String clientSecret() {
        return clientSecret;
    }

    @Override
    public String redirectUri() {
        return redirectUri;
    }

    @Override
    public Set<String> scope() {
        return scope;
    }
}
