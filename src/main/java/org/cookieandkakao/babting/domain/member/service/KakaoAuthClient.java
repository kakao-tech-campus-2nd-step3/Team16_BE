package org.cookieandkakao.babting.domain.member.service;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import org.cookieandkakao.babting.common.exception.customexception.ApiException;
import org.cookieandkakao.babting.common.properties.KakaoProviderProperties;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberInfoGetResponse;
import org.cookieandkakao.babting.domain.member.dto.KakaoTokenGetResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoAuthClient {

    private final KakaoProviderProperties kakaoProviderProperties;
    private final RestClient restClient;

    public KakaoAuthClient(KakaoProviderProperties kakaoProviderProperties, RestClient restClient) {
        this.kakaoProviderProperties = kakaoProviderProperties;
        this.restClient = restClient;
    }

    public KakaoTokenGetResponse callTokenApi(MultiValueMap<String, String> body) {

        String tokenUri = kakaoProviderProperties.tokenUri();

        return restClient.post()
            .uri(tokenUri)
            .contentType(APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                throw new ApiException("카카오 토큰 발급 실패");
            })
            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                throw new ApiException("카카오 인증 서버 에러");
            })
            .toEntity(KakaoTokenGetResponse.class)
            .getBody();
    }

    public KakaoMemberInfoGetResponse callMemberInfoApi(String accessToken) {
        String userInfoUri = kakaoProviderProperties.userInfoUri();

        return restClient.get()
            .uri(userInfoUri)
            .header("Authorization", "Bearer " + accessToken)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                throw new ApiException("카카오 사용자 정보 조회 실패");
            })
            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                throw new ApiException("카카오 사용자 정보 서버 에러");
            })
            .toEntity(KakaoMemberInfoGetResponse.class)
            .getBody();
    }

    public void callUnlinkApi(String accessToken) {
        String unlinkUri = kakaoProviderProperties.unlinkUri();

        restClient.get()
            .uri(unlinkUri)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                throw new ApiException("카카오 연결 끊기 실패");
            })
            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                throw new ApiException("카카오 인증 서버 에러");
            })
            .toBodilessEntity();

    }

}
