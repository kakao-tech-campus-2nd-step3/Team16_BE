package org.cookieandkakao.babting.domain.calendar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cookieandkakao.babting.domain.calendar.dto.request.EventCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.request.TimeCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventDetailGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponse;
import org.cookieandkakao.babting.domain.member.entity.KakaoToken;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


class TalkCalendarServiceTest {

    @InjectMocks
    private TalkCalendarService talkCalendarService;

    @Mock
    private TalkCalendarClientService talkCalendarClientService;

    @Mock
    private EventService eventService;

    @Mock
    private MemberService memberService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUpdatedEventListTest() {
        // Given
        Long memberId = 1L;
        String from = "2024-10-01T00:00:00Z";
        String to = "2024-10-31T23:59:59Z";
        EventGetResponse eventGetResponse = new EventGetResponse("testId", "Test Title",
            "USER", "calendarId", null, false, false, null, null,
            "Test Description", null, null, "TestColor", null);
        EventListGetResponse eventListGetResponse = new EventListGetResponse(
            new ArrayList<>(List.of((eventGetResponse))));
        String accessToken = "testAccessToken";
        EventDetailGetResponse eventDetailGetResponseMock = new EventDetailGetResponse(
            eventGetResponse);

        // Mocking
        given(memberService.getKakaoToken(memberId)).willReturn(
            new KakaoToken(accessToken, null, null, null));
        given(talkCalendarClientService.getEventList(accessToken, from, to)).willReturn(
            eventListGetResponse);
        given(talkCalendarClientService.getEvent(accessToken, eventGetResponse.id())).willReturn(
            eventDetailGetResponseMock);

        // When
        List<EventGetResponse> result = talkCalendarService.getUpdatedEventList(from, to, memberId);

        // Then
        verify(memberService).getKakaoToken(any(Long.class));
        verify(talkCalendarClientService).getEventList(accessToken, from, to);
        verify(talkCalendarClientService).getEvent(accessToken, eventGetResponse.id());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testId", result.get(0).id());
    }

    @Test
    void getEventTest() {
        // Given
        Long memberId = 1L;
        String eventId = "testId";
        String accessToken = "testAccessToken";
        EventGetResponse eventGetResponse = new EventGetResponse("testId", "Test Title",
            "USER", "calendarId", null, false, false, null, null,
            "Test Description", null, null, "TestColor", null);
        EventDetailGetResponse eventDetailGetResponseMock = new EventDetailGetResponse(
            eventGetResponse);

        // Mocking
        given(memberService.getKakaoToken(memberId)).willReturn(
            new KakaoToken(accessToken, null, null, null));
        given(talkCalendarClientService.getEvent(accessToken, eventId)).willReturn(
            eventDetailGetResponseMock);

        // When
        EventDetailGetResponse result = talkCalendarService.getEvent(memberId, eventId);

        // Then
        verify(memberService).getKakaoToken(any(Long.class));
        verify(talkCalendarClientService).getEvent(accessToken, eventId);
        assertNotNull(result);
        assertEquals(eventId, result.event().id());
    }

    @Test
    void createEventTest() {
        // Given
        Long memberId = 1L;
        TimeCreateRequest timeRequest = new TimeCreateRequest("2024-10-01T00:00:00Z",
            "2024-10-01T03:00:00Z", "Asia/Seoul", false);
        EventCreateRequest eventCreateRequest = new EventCreateRequest("testTitle", timeRequest,
            null, null);
        String accessToken = "testAccessToken";
        String eventId = "testEventId";
        String eventJson = convertToJSONString(eventCreateRequest);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("event", eventJson);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("event_id", eventId);

        // Mocking
        given(memberService.getKakaoToken(memberId)).willReturn(
            new KakaoToken(accessToken, null, null, null));
        given(talkCalendarClientService.createEvent(accessToken, formData)).willReturn(
            responseBody);

        // When
        EventCreateResponse result = talkCalendarService.createEvent(eventCreateRequest, memberId);

        // Then
        verify(memberService).getKakaoToken(any(Long.class));
        verify(talkCalendarClientService).createEvent(accessToken, formData);
        assertNotNull(result);
        assertEquals(eventId, result.eventId());
    }

    private String convertToJSONString(EventCreateRequest eventCreateRequest) {
        try {
            return objectMapper.writeValueAsString(eventCreateRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
