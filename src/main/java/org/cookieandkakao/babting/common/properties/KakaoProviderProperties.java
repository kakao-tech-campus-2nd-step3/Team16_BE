package org.cookieandkakao.babting.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth2.provider.kakao")
public record KakaoProviderProperties(
    String authorizationUri,
    String tokenUri,
    String userInfoUri,
    String calendarEventListUri,
    String calendarEventDetailUri,
    String calendarCreateEventUri
) {

}