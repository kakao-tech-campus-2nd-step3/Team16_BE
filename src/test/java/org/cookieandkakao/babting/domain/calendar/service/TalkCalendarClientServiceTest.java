package org.cookieandkakao.babting.domain.calendar.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.cookieandkakao.babting.common.properties.KakaoProviderProperties;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

@RestClientTest(KakaoProviderProperties.class)
class TalkCalendarClientServiceTest {

    @InjectMocks
    private TalkCalendarClientService talkCalendarClientService;

    @Mock
    private KakaoProviderProperties kakaoProviderProperties;

    @Mock
    private RestClient restClient;

    private RequestBodyUriSpec requestBodyUriSpec;
    private ResponseSpec responseSpec;
    private RequestHeadersUriSpec requestHeadersUriSpec;

    private static MockedStatic<ResponseEntity> responseMockedStatic;

    @BeforeAll
    public static void beforeAll() {
        responseMockedStatic = mockStatic(ResponseEntity.class);
    }

    @AfterAll
    public static void afterAll() {
        responseMockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestBodyUriSpec = mock(RequestBodyUriSpec.class);
        responseSpec = mock(ResponseSpec.class);
        requestHeadersUriSpec = mock(RequestHeadersUriSpec.class);
    }

    @Test
    void getEventListTest() {
        // Given
        String accessToken = "testAccessToken";
        String from = "2024-10-01T00:00:00Z";
        String to = "2024-10-31T23:59:59Z";
        String url = "https://kapi.kakao.com/v2/api/calendar/events";
        URI uri = buildUri(url, from, to);

        EventGetResponse eventGetResponse = new EventGetResponse("testId", "Test Title",
            "USER", "calendarId", null, false, false, null, null,
            "Test Description", null, null, "TestColor", null);
        EventListGetResponse eventListGetResponseMock = new EventListGetResponse(
            new ArrayList<>(List.of(eventGetResponse)));

        // Mocking
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(anyString(), anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        responseMockedStatic.when(() -> ResponseEntity.ok(any(EventListGetResponse.class))).thenReturn(ResponseEntity.ok(eventListGetResponseMock));
        when(responseSpec.toEntity(EventListGetResponse.class)).thenReturn(ResponseEntity.ok(eventListGetResponseMock));

        // When
        EventListGetResponse result = talkCalendarClientService.getEventList(accessToken, from, to);

        // Then
        assertNotNull(result);
        assertEquals(eventGetResponse.id(), result.events().get(0).id());

        // Verify
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(uri);
        verify(requestBodyUriSpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        verify(requestBodyUriSpec).retrieve();
        verify(responseSpec).toEntity(EventListGetResponse.class);
    }

    private URI buildUri(String baseUrl, String from, String to) {
        return URI.create(
            String.format("%s?from=%s&to=%s&limit=100&time_zone=Asia/Seoul", baseUrl, from, to));
    }
}
