package org.cookieandkakao.babting.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient kakaoRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5초
        factory.setReadTimeout(5000); // 5초

        return RestClient.builder()
            .baseUrl("https://kapi.kakao.com")
            .defaultHeader("Content-Type", "application/json")
            .requestFactory(factory)
            .build();
    }

}
