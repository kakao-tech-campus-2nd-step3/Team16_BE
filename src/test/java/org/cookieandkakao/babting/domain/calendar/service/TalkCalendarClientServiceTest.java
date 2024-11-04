package org.cookieandkakao.babting.domain.calendar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import org.cookieandkakao.babting.common.exception.customexception.ApiException;
import org.cookieandkakao.babting.common.properties.KakaoProviderProperties;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventDetailGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;


@ExtendWith(MockitoExtension.class)
class TalkCalendarClientServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private KakaoProviderProperties kakaoProviderProperties;

    @InjectMocks
    private TalkCalendarClientService talkCalendarClientService;

    @Mock
    private RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private ResponseSpec responseSpec;

    @Mock
    private RequestBodySpec requestBodySpec;

    @Mock
    private RequestHeadersUriSpec requestHeadersUriSpec;

    private static final String ACCESS_TOKEN = "testAccessToken";
    private static final String EVENT_ID = "testId";


    @Test
    void getEventListTest() {
        // Given
        String relativeUrl = "test";
        String from = "test From";
        String to = "test To";
        EventGetResponse eventGetResponse = new EventGetResponse("testId", "test title", "USER",
            null, false, false, null, null, "test description", null, null, null, null);
        EventListGetResponse eventListGetResponse = new EventListGetResponse(
            List.of(eventGetResponse));
        ResponseEntity<EventListGetResponse> responseEntity = new ResponseEntity<>(
            eventListGetResponse,
            HttpStatus.OK);

        // Mocking
        given(kakaoProviderProperties.calendarEventListUri()).willReturn(relativeUrl);
        given(restClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri(any(Function.class))).will(invocationOnMock -> {
            Function<UriBuilder, URI> uriFunction = invocationOnMock.getArgument(0);
            URI uri = uriFunction.apply(UriComponentsBuilder.fromPath(relativeUrl));
            return requestHeadersUriSpec;
        });
        given(requestHeadersUriSpec.header(anyString(), anyString())).willReturn(
            requestBodyUriSpec);
        given(requestBodyUriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.toEntity(EventListGetResponse.class)).willReturn(responseEntity);

        // When
        EventListGetResponse result = talkCalendarClientService.getEventList(ACCESS_TOKEN, from,
            to);

        // Then
        assertNotNull(result);
        assertEquals(result, eventListGetResponse);
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersUriSpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN);
        verify(requestBodyUriSpec).retrieve();
        verify(responseSpec).toEntity(EventListGetResponse.class);
    }

    @Test
    void getEventTest() {
        // Given
        String relativeUrl = "test";
        EventGetResponse eventGetResponse = new EventGetResponse("testId", "test title", "USER",
            null, false, false, null, null, "test description", null, null, null, null);
        EventDetailGetResponse eventDetailGetResponse = new EventDetailGetResponse(
            eventGetResponse);
        ResponseEntity<EventDetailGetResponse> responseEntity = new ResponseEntity<>(
            eventDetailGetResponse,
            HttpStatus.OK);

        // Mocking
        given(kakaoProviderProperties.calendarEventDetailUri()).willReturn(relativeUrl);
        given(restClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri(any(Function.class))).will(invocationOnMock -> {
            Function<UriBuilder, URI> uriFunction = invocationOnMock.getArgument(0);
            URI uri = uriFunction.apply(UriComponentsBuilder.fromPath(relativeUrl));
            return requestHeadersUriSpec;
        });
        given(requestHeadersUriSpec.header(anyString(), anyString())).willReturn(
            requestBodyUriSpec);
        given(requestBodyUriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.toEntity(EventDetailGetResponse.class)).willReturn(responseEntity);

        // When
        EventDetailGetResponse result = talkCalendarClientService.getEvent(ACCESS_TOKEN, EVENT_ID);

        // Then
        assertNotNull(result);
        assertEquals(result, eventDetailGetResponse);
        assertEquals(result.event().id(), "testId");
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersUriSpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN);
        verify(requestBodyUriSpec).retrieve();
        verify(responseSpec).toEntity(EventDetailGetResponse.class);
    }

    @Test
    void createEventTest() {
        // Given
        String relativeUrl = "test";
        EventCreateResponse eventCreateResponse = new EventCreateResponse("testId");
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        ResponseEntity<EventCreateResponse> responseEntity = new ResponseEntity<>(
            eventCreateResponse,
            HttpStatus.CREATED);

        // Mocking
        given(kakaoProviderProperties.calendarCreateEventUri()).willReturn(relativeUrl);
        given(restClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.uri(anyString())).willReturn(requestBodySpec);
        given(requestBodySpec.header(anyString(), anyString())).willReturn(requestBodySpec);
        given(requestBodySpec.header(anyString(), anyString())).willReturn(requestBodySpec);
        given(requestBodySpec.body(any(MultiValueMap.class))).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.toEntity(EventCreateResponse.class)).willReturn(responseEntity);

        // When
        EventCreateResponse result = talkCalendarClientService.createEvent(ACCESS_TOKEN, formData);

        // Then
        assertNotNull(result);
        assertEquals(result, eventCreateResponse);
        assertEquals(result.eventId(), "testId");
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(relativeUrl);
        verify(requestBodySpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN);
        verify(requestBodySpec).header(HttpHeaders.CONTENT_TYPE,
            "application/x-www-form-urlencoded");
        verify(requestBodySpec).body(formData);
        verify(requestBodySpec).retrieve();
        verify(responseSpec).toEntity(EventCreateResponse.class);
    }

    @Test
    void getEventListTest_RestClientException() {
        // Given
        String relativeUrl = "test";
        String from = "test From";
        String to = "test To";

        // Mocking
        given(kakaoProviderProperties.calendarEventListUri()).willReturn(relativeUrl);
        given(restClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri(any(Function.class))).will(invocationOnMock -> {
            Function<UriBuilder, URI> uriFunction = invocationOnMock.getArgument(0);
            URI uri = uriFunction.apply(UriComponentsBuilder.fromPath(relativeUrl));
            return requestHeadersUriSpec;
        });
        given(requestHeadersUriSpec.header(anyString(), anyString())).willReturn(
            requestBodyUriSpec);
        given(requestBodyUriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.toEntity(EventListGetResponse.class)).willThrow(
            new RestClientException("API 에러"));

        // When
        Exception e = assertThrows(
            ApiException.class,
            () -> talkCalendarClientService.getEventList(ACCESS_TOKEN, from, to));

        // Then
        assertNotNull(e);
        assertEquals(e.getClass(), ApiException.class);
        assertEquals(e.getMessage(), "일정 목록 조회 중 에러 발생 : API 에러");
        verify(kakaoProviderProperties).calendarEventListUri();
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersUriSpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN);
        verify(requestBodyUriSpec).retrieve();
        verify(responseSpec).toEntity(EventListGetResponse.class);
    }

    @Test
    void getEventTest_RestClientException() {
        // Given
        String relativeUrl = "test";

        // Mocking
        given(kakaoProviderProperties.calendarEventDetailUri()).willReturn(relativeUrl);
        given(restClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri(any(Function.class))).will(invocationOnMock -> {
            Function<UriBuilder, URI> uriFunction = invocationOnMock.getArgument(0);
            URI uri = uriFunction.apply(UriComponentsBuilder.fromPath(relativeUrl));
            return requestHeadersUriSpec;
        });
        given(requestHeadersUriSpec.header(anyString(), anyString())).willReturn(
            requestBodyUriSpec);
        given(requestBodyUriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.toEntity(EventDetailGetResponse.class)).willThrow(
            new RestClientException("API 에러"));

        // When
        Exception e = assertThrows(
            ApiException.class, () -> talkCalendarClientService.getEvent(ACCESS_TOKEN, EVENT_ID)
        );

        // Then
        assertNotNull(e);
        assertEquals(e.getClass(), ApiException.class);
        assertEquals(e.getMessage(), "일정 상세 조회 중 오류 발생 : API 에러");
        verify(kakaoProviderProperties).calendarEventDetailUri();
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersUriSpec).header(anyString(), anyString());
        verify(requestBodyUriSpec).retrieve();
        verify(responseSpec).toEntity(EventDetailGetResponse.class);
    }

    @Test
    void createEventTest_RestClientException() {
        // Given
        String relativeUrl = "test";
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        // Mocking
        given(kakaoProviderProperties.calendarCreateEventUri()).willReturn(relativeUrl);
        given(restClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.uri(anyString())).willReturn(requestBodySpec);
        given(requestBodySpec.header(anyString(), anyString())).willReturn(requestBodySpec);
        given(requestBodySpec.header(anyString(), anyString())).willReturn(requestBodySpec);
        given(requestBodySpec.body(any(MultiValueMap.class))).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.toEntity(EventCreateResponse.class)).willThrow(
            new RestClientException("API 에러"));

        // When
        Exception e = assertThrows(
            ApiException.class, () -> talkCalendarClientService.createEvent(ACCESS_TOKEN, formData)
        );

        // Then
        assertNotNull(e);
        assertEquals(e.getClass(), ApiException.class);
        assertEquals(e.getMessage(), "일정 생성 중 오류 발생 : API 에러");
        verify(kakaoProviderProperties).calendarCreateEventUri();
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(anyString());
        verify(requestBodySpec, times(2)).header(anyString(), anyString());
        verify(requestBodySpec).body(any(MultiValueMap.class));
        verify(requestBodySpec).retrieve();
        verify(responseSpec).toEntity(EventCreateResponse.class);
    }

    @Test
    void getEventListTest_알수없는_예외_발생시_예외처리() {
        // Given
        String from = "2024-01-01";
        String to = "2024-01-10";

        // Mocking
        given(restClient.get()).willThrow(new RuntimeException("API 에러"));

        // When
        Exception e = assertThrows(RuntimeException.class,
            () -> talkCalendarClientService.getEventList(ACCESS_TOKEN, from, to));

        // Then
        assertNotNull(e);
        assertEquals(e.getClass(), RuntimeException.class);
        assertEquals(e.getMessage(), "API 에러");
        verify(restClient).get();
    }

    @Test
    void getEventTest_알수없는_예외_발생시_예외처리() {
        // Mocking
        given(restClient.get()).willThrow(new RuntimeException("API 에러"));

        // When
        Exception e = assertThrows(RuntimeException.class,
            () -> talkCalendarClientService.getEvent(ACCESS_TOKEN, EVENT_ID));

        // Then
        assertNotNull(e);
        assertEquals(e.getClass(), RuntimeException.class);
        assertEquals(e.getMessage(), "API 에러");
        verify(restClient).get();
    }

    @Test
    void createEventTest_알수없는_예외_발생시_예외처리() {
        // Given
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        // Mocking
        given(restClient.post()).willThrow(new RuntimeException("API 에러"));

        // When
        Exception e = assertThrows(RuntimeException.class,
            () -> talkCalendarClientService.createEvent(ACCESS_TOKEN, formData));

        // Then
        assertNotNull(e);
        assertEquals(e.getClass(), RuntimeException.class);
        assertEquals(e.getMessage(), "API 에러");
        verify(restClient).post();
    }
}