package org.cookieandkakao.babting.domain.member.util;


import org.cookieandkakao.babting.common.properties.KakaoClientProperties;
import org.cookieandkakao.babting.common.properties.KakaoProviderProperties;

public abstract class OAuthUriBuilder {

    KakaoClientProperties clientProperties;
    KakaoProviderProperties providerProperties;

    public OAuthUriBuilder clientProperties(KakaoClientProperties clientProperties) {
        this.clientProperties = clientProperties;
        return this;
    }

    public OAuthUriBuilder providerProperties(KakaoProviderProperties providerProperties) {
        this.providerProperties = providerProperties;
        return this;
    }

    abstract public String build();

}
