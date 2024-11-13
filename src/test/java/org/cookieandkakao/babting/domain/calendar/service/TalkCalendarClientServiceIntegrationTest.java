package org.cookieandkakao.babting.domain.calendar.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.cookieandkakao.babting.common.exception.customexception.ApiException;
import org.cookieandkakao.babting.common.properties.KakaoProviderProperties;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventDetailGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class TalkCalendarClientServiceIntegrationTest {

    @Mock
    private KakaoProviderProperties providerProperties;

    private RestClient.Builder restClientBuilder = RestClient.builder();

    private MockRestServiceServer mockServer;

    private TalkCalendarClientService talkCalendarClientService;

    private final String ACCESS_TOKEN = "testAccessToken";
    private final String EVENT_ID = "testEventId";
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        talkCalendarClientService = new TalkCalendarClientService(restClientBuilder.build(),
            providerProperties);
    }

    @Nested
    class 일정_목록_조회_통합_테스트 {

        @Test
        void 성공() throws JsonProcessingException {
            // Given
            String from = "2024-09-30T00:00:00Z";
            String to = "2024-10-30T10:00:00Z";
            String relativeUrl = "/events";
            EventGetResponse eventGetResponse = new EventGetResponse("testId", "test title", "USER",
                null, false, false, null, null, "test description", null, null, null, null);
            EventListGetResponse mockResponse = new EventListGetResponse(List.of(eventGetResponse));
            String fullUrl = UriComponentsBuilder.fromPath(relativeUrl)
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("limit", 100)
                .queryParam("time_zone", "Asia/Seoul")
                .build().toUriString();

            // Mocking
            given(providerProperties.calendarEventListUri()).willReturn(relativeUrl);
            mockServer.expect(requestTo(fullUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockResponse),
                    MediaType.APPLICATION_JSON));

            // When
            EventListGetResponse response = talkCalendarClientService.getEventList(ACCESS_TOKEN,
                from,
                to);

            // Then
            assertThat(response).isEqualTo(mockResponse);
            assertThat(response.events().get(0)).isEqualTo(eventGetResponse);
        }

        @Test
        void 실패_ApiException예외() {
            // Given
            String from = "2024-09-30T00:00:00Z";
            String to = "2024-10-30T10:00:00Z";
            String relativeUrl = "/events";
            String fullUrl = UriComponentsBuilder.fromPath(relativeUrl)
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("limit", 100)
                .queryParam("time_zone", "Asia/Seoul")
                .build().toUriString();

            // Mocking
            given(providerProperties.calendarEventListUri()).willReturn(relativeUrl);
            mockServer.expect(requestTo(fullUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withBadRequest());

            // When & Then
            assertThatThrownBy(() -> talkCalendarClientService.getEventList(ACCESS_TOKEN, from, to))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("일정 목록 조회 중 에러 발생 : ");
        }

        @Test
        void 실패_알수없는_예외_발생시_예외처리() {
            // Given
            String from = "2024-09-30T00:00:00Z";
            String to = "2024-10-30T10:00:00Z";
            String relativeUrl = "/events";
            String fullUrl = UriComponentsBuilder.fromPath(relativeUrl)
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("limit", 100)
                .queryParam("time_zone", "Asia/Seoul")
                .build().toUriString();

            // Mocking
            given(providerProperties.calendarEventListUri()).willReturn(relativeUrl);
            mockServer.expect(requestTo(fullUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withBadRequest());

            // When & Then
            assertThatThrownBy(() -> talkCalendarClientService.getEventList(ACCESS_TOKEN, from, to))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("일정 목록 조회 중 에러 발생 : ");
        }
    }

    @Nested
    class 일정_상세_조회_통합_테스트 {

        @Test
        void 성공() throws JsonProcessingException {
            // Given
            String relativeUrl = "/event";
            EventGetResponse eventGetResponse = new EventGetResponse("testId", "test title", "USER",
                null, false, false, null, null, "test description", null, null, null, null);
            EventDetailGetResponse mockResponse = new EventDetailGetResponse(
                eventGetResponse);
            String fullUrl = UriComponentsBuilder.fromPath(relativeUrl)
                .queryParam("event_id", EVENT_ID)
                .build().toUriString();

            // Mocking
            given(providerProperties.calendarEventDetailUri()).willReturn(relativeUrl);
            mockServer.expect(requestTo(fullUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockResponse),
                    MediaType.APPLICATION_JSON));

            // When
            EventDetailGetResponse response = talkCalendarClientService.getEvent(ACCESS_TOKEN,
                EVENT_ID);

            // Then
            assertThat(response).isEqualTo(mockResponse);
            assertThat(eventGetResponse).isEqualTo(mockResponse.event());
        }

        @Test
        void 실패_ApiException예외() {
            String relativeUrl = "/event";
            String fullUrl = UriComponentsBuilder.fromPath(relativeUrl)
                .queryParam("event_id", EVENT_ID)
                .build().toUriString();

            // Mocking
            given(providerProperties.calendarEventDetailUri()).willReturn(relativeUrl);
            mockServer.expect(requestTo(fullUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withBadRequest());

            // When & Then
            assertThatThrownBy(() -> talkCalendarClientService.getEvent(ACCESS_TOKEN, EVENT_ID))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("일정 상세 조회 중 오류 발생 : ");
        }

        @Test
        void 실패_알수없는_예외_발생시_예외처리() {
            // Given
            String relativeUrl = "/event";
            String fullUrl = UriComponentsBuilder.fromPath(relativeUrl)
                .queryParam("event_id", EVENT_ID)
                .build().toUriString();

            // Mocking
            given(providerProperties.calendarEventDetailUri()).willReturn(relativeUrl);
            mockServer.expect(requestTo(fullUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withBadRequest());

            // When & Then
            assertThatThrownBy(() -> talkCalendarClientService.getEvent(ACCESS_TOKEN, EVENT_ID))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("일정 상세 조회 중 오류 발생 : ");
        }
    }

    @Nested
    class 일정_생성_통합_테스트 {

        @Test
        void 성공() throws JsonProcessingException {
            // Given
            String relativeUrl = "/create/event";
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            EventCreateResponse mockResponse = new EventCreateResponse(EVENT_ID);

            // Mocking
            given(providerProperties.calendarCreateEventUri()).willReturn(relativeUrl);
            mockServer.expect(requestTo(relativeUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockResponse),
                    MediaType.APPLICATION_JSON));

            // When
            EventCreateResponse response = talkCalendarClientService.createEvent(ACCESS_TOKEN,
                formData);

            // Then
            assertThat(response).isEqualTo(mockResponse);
            assertThat(response.eventId()).isEqualTo(mockResponse.eventId());
        }

        @Test
        void 실패_ApiException예외() {
            // Given
            String relativeUrl = "/create/event";
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

            // Mocking
            given(providerProperties.calendarCreateEventUri()).willReturn(relativeUrl);
            mockServer.expect(requestTo(relativeUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest());

            // When & Then
            assertThatThrownBy(() -> talkCalendarClientService.createEvent(ACCESS_TOKEN, formData))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("일정 생성 중 오류 발생 : ");
        }

        @Test
        void 실패_알수없는_예외_발생시_예외처리() {
            // Given
            String relativeUrl = "/create/event";
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

            // Mocking
            given(providerProperties.calendarCreateEventUri()).willReturn(relativeUrl);
            mockServer.expect(requestTo(relativeUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest());

            // When & Then
            assertThatThrownBy(() -> talkCalendarClientService.createEvent(ACCESS_TOKEN, formData))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("일정 생성 중 오류 발생 : ");
        }
    }
}
