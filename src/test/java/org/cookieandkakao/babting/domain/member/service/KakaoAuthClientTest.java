package org.cookieandkakao.babting.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.cookieandkakao.babting.common.exception.customexception.ApiException;
import org.cookieandkakao.babting.common.properties.KakaoProviderProperties;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberInfoGetResponse;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberProfileGetResponse;
import org.cookieandkakao.babting.domain.member.dto.KakaoTokenGetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class KakaoAuthClientTest {

    @Mock
    private KakaoProviderProperties providerProperties;

    private RestClient.Builder restClientBuilder = RestClient.builder();

    private MockRestServiceServer mockRestServiceServer;

    private KakaoAuthClient kakaoAuthClient;

    private final String TOKEN_URI = "https://test-token-uri.com";
    private final String USER_INFO_URI = "https://test-user-info-uri.com";
    private final String UNLINK_URI = "https://test-unlink-uri.com";
    private final String ACCESS_TOKEN = "TestAccessToken";

    @BeforeEach
    void setup() {
        mockRestServiceServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        kakaoAuthClient = new KakaoAuthClient(providerProperties, restClientBuilder.build());
    }

    @Test
    @DisplayName("카카오 토큰을 발급받을 수 있다.")
    void callTokenApi() throws JsonProcessingException {
        // given
        given(providerProperties.tokenUri()).willReturn(TOKEN_URI);

        KakaoTokenGetResponse kakaoToken = new KakaoTokenGetResponse(ACCESS_TOKEN, 1,
            "refreshToken", 2);
        ObjectMapper mapper = new ObjectMapper();
        String expectedResult = mapper.writeValueAsString(kakaoToken);

        mockRestServiceServer.expect(requestTo(TOKEN_URI))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(expectedResult, MediaType.APPLICATION_JSON));

        // when
        KakaoTokenGetResponse actual = kakaoAuthClient.callTokenApi(new LinkedMultiValueMap<>());

        // then
        Assertions.assertThat(actual).isEqualTo(kakaoToken);
    }

    @Test
    @DisplayName("카카오 토큰 발급 실패시 ApiException 을 발생시킨다.")
    void callTokenApiFailed() {
        // given
        given(providerProperties.tokenUri()).willReturn(TOKEN_URI);

        mockRestServiceServer.expect(requestTo(TOKEN_URI))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withBadRequest());

        // when
        // then
        assertThatThrownBy(() -> kakaoAuthClient.callTokenApi(new LinkedMultiValueMap<>()))
            .isInstanceOf(ApiException.class);

    }

    @Test
    @DisplayName("카카오 회원 정보를 가져올 수 있다.")
    void callMemberInfoApi() throws JsonProcessingException {
        // given
        given(providerProperties.userInfoUri()).willReturn(USER_INFO_URI);

        KakaoMemberProfileGetResponse kakaoMemberProfile = new KakaoMemberProfileGetResponse(
            "nickname", "profile.jpg", "thumbnail.jpg");
        KakaoMemberInfoGetResponse kakaoMemberInfo = new KakaoMemberInfoGetResponse(1L,
            kakaoMemberProfile);
        ObjectMapper mapper = new ObjectMapper();
        String expectedResult = mapper.writeValueAsString(kakaoMemberInfo);

        mockRestServiceServer.expect(requestTo(USER_INFO_URI))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(expectedResult, MediaType.APPLICATION_JSON));

        // when
        KakaoMemberInfoGetResponse actual = kakaoAuthClient.callMemberInfoApi(ACCESS_TOKEN);

        // then
        Assertions.assertThat(actual).isEqualTo(kakaoMemberInfo);
    }

    @Test
    @DisplayName("카카오 회원 정보 조회 실패시 ApiException 을 발생시킨다.")
    void callMemberInfoApiFail() {
        // given
        given(providerProperties.userInfoUri()).willReturn(USER_INFO_URI);

        mockRestServiceServer.expect(requestTo(USER_INFO_URI))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withBadRequest());

        // when
        // then
        assertThatThrownBy(() -> kakaoAuthClient.callMemberInfoApi(ACCESS_TOKEN))
            .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("카카오 회원 연결을 끊을 수 있다.")
    void callUnlinkApi() throws JsonProcessingException {
        // given
        given(providerProperties.unlinkUri()).willReturn(UNLINK_URI);

        mockRestServiceServer.expect(requestTo(UNLINK_URI))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess());

        // when
        // then
        assertThatCode(() -> kakaoAuthClient.callUnlinkApi(ACCESS_TOKEN))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("카카오 회원 연결 끊기 실패시 ApiException 을 발생시킨다.")
    void callUnlinkApiFail() {
        // given
        given(providerProperties.unlinkUri()).willReturn(UNLINK_URI);

        mockRestServiceServer.expect(requestTo(UNLINK_URI))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withServerError());

        // when
        // then
        assertThatThrownBy(() -> kakaoAuthClient.callUnlinkApi(ACCESS_TOKEN))
            .isInstanceOf(ApiException.class);
    }
}