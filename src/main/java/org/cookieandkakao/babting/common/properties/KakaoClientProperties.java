package org.cookieandkakao.babting.common.properties;

import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth2.client.kakao")
public record KakaoClientProperties(
    String clientId,
    String clientSecret,
    String redirectUri,
    Set<String> scope) {

}