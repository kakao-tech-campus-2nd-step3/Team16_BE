package org.cookieandkakao.babting.domain.calendar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.cookieandkakao.babting.domain.calendar.exception.EventCreationException;
import org.cookieandkakao.babting.domain.calendar.dto.request.EventCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.request.TimeCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventDetailGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponse;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), anyString(), any(Duration.class));
        given(valueOperations.get(any(String.class))).willReturn(null);
    }

    @Test
    void getUpdatedEventListTest() {
        // Given
        Long memberId = 1L;
        String from = "2024-10-01T00:00:00Z";
        String to = "2024-10-31T23:59:59Z";
        EventGetResponse eventGetResponse = new EventGetResponse("testId", "Test Title",
            "USER", null, false, false, null, null,
            "Test Description", null, null, "TestColor", null);
        EventListGetResponse eventListGetResponse = new EventListGetResponse(
            new ArrayList<>(List.of((eventGetResponse))));
        String accessToken = "testAccessToken";
        EventDetailGetResponse eventDetailGetResponseMock = new EventDetailGetResponse(
            eventGetResponse);
        // Mocking
        given(memberService.getKakaoAccessToken(memberId)).willReturn(accessToken);
        given(talkCalendarClientService.getEventList(accessToken, from, to)).willReturn(
            eventListGetResponse);
        given(talkCalendarClientService.getEvent(accessToken, eventGetResponse.id())).willReturn(
            eventDetailGetResponseMock);

        // When
        List<EventGetResponse> result = talkCalendarService.getUpdatedEventList(from, to, memberId);

        // Then
        verify(memberService).getKakaoAccessToken(memberId);
        verify(talkCalendarClientService).getEventList(accessToken, from, to);
        verify(talkCalendarClientService).getEvent(accessToken, eventGetResponse.id());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testId", result.getFirst().id());
    }

    @Test
    void getEventTest() {
        // Given
        Long memberId = 1L;
        String eventId = "testId";
        String accessToken = "testAccessToken";
        EventGetResponse eventGetResponse = new EventGetResponse("testId", "Test Title",
            "USER", null, false, false, null, null,
            "Test Description", null, null, "TestColor", null);
        EventDetailGetResponse eventDetailGetResponseMock = new EventDetailGetResponse(
            eventGetResponse);

        // Mocking
        given(memberService.getKakaoAccessToken(memberId)).willReturn(accessToken);
        given(talkCalendarClientService.getEvent(accessToken, eventId)).willReturn(
            eventDetailGetResponseMock);

        // When
        EventDetailGetResponse result = talkCalendarService.getEvent(memberId, eventId);

        // Then
        verify(memberService).getKakaoAccessToken(any(Long.class));
        verify(talkCalendarClientService).getEvent(accessToken, eventId);
        assertNotNull(result);
        assertEquals(eventId, result.event().id());
    }

    @Test
    void createEventTest() throws JsonProcessingException {
        // Given
        Long memberId = 1L;
        TimeCreateRequest timeRequest = new TimeCreateRequest("2024-10-01T00:00:00Z",
            "2024-10-01T03:00:00Z", "Asia/Seoul", false);
        EventCreateRequest eventCreateRequest = new EventCreateRequest("testTitle", timeRequest,
            null, null, null);
        String accessToken = "testAccessToken";
        String eventId = "testEventId";
        String eventJson = objectMapper.writeValueAsString(eventCreateRequest);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("event", eventJson);
        EventCreateResponse responseBody = new EventCreateResponse(eventId);

        // Mocking
        given(memberService.getKakaoAccessToken(memberId)).willReturn(accessToken);
        given(talkCalendarClientService.createEvent(accessToken, formData)).willReturn(
            responseBody);

        // When
        EventCreateResponse result = talkCalendarService.createEvent(eventCreateRequest, memberId);

        // Then
        verify(memberService).getKakaoAccessToken(any(Long.class));
        verify(talkCalendarClientService).createEvent(accessToken, formData);
        assertNotNull(result);
        assertEquals(eventId, result.eventId());
    }

    @Test
    void getEventTest_InvalidMemberId() {
        // Given
        Long memberId = null;
        String eventId = "testId";

        // Mocking
        given(memberService.getKakaoAccessToken(memberId)).willThrow(
            new IllegalArgumentException("존재하지 않는 MemberID입니다."));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> talkCalendarService.getEvent(memberId, eventId));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(), "존재하지 않는 MemberID입니다.");
        verify(memberService).getKakaoAccessToken(memberId);
    }

    @Test
    void getEventTest_InvalidEventId() {
        // Given
        Long memberId = 1L;
        String eventId = null;
        String accessToken = "testAccessToken";

        // Mocking
        given(memberService.getKakaoAccessToken(memberId)).willReturn(accessToken);
        given(talkCalendarClientService.getEvent(accessToken, eventId)).willThrow(
            new IllegalArgumentException("존재하지 않는 EventID입니다."));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> talkCalendarService.getEvent(memberId, eventId));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(), "존재하지 않는 EventID입니다.");
        verify(memberService).getKakaoAccessToken(memberId);
        verify(talkCalendarClientService).getEvent(accessToken, eventId);
    }

    @Test
    void getUpdatedEventListTest_InvalidMemberId() {
        // Given
        Long memberId = null;
        String from = "2024-10-01T00:00:00Z";
        String to = "2024-10-31T23:59:59Z";

        // Mocking
        given(memberService.getKakaoAccessToken(memberId)).willThrow(
            new IllegalArgumentException("존재하지 않는 MemberID입니다."));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> talkCalendarService.getUpdatedEventList(from, to, memberId));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(), "존재하지 않는 MemberID입니다.");
        verify(memberService).getKakaoAccessToken(memberId);
    }

    @Test
    void getUpdatedEventListTest_InvalidFrom() {
        // Given
        Long memberId = 1L;
        String from = null;
        String to = "2024-10-31T23:59:59Z";
        String accessToken = "testAccessToken";

        // Mocking
        given(memberService.getKakaoAccessToken(memberId)).willReturn(accessToken);
        given(talkCalendarClientService.getEventList(accessToken, from, to)).willThrow(
            new IllegalArgumentException("잘못된 From 값 입니다."));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> talkCalendarService.getUpdatedEventList(from, to, memberId));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(), "잘못된 From 값 입니다.");
        verify(memberService).getKakaoAccessToken(any(Long.class));
        verify(talkCalendarClientService).getEventList(accessToken, from, to);
    }

    @Test
    void getUpdatedEventListTest_InvalidTo() {
        // Given
        Long memberId = 1L;
        String from = "2024-10-01T00:00:00Z";
        String to = null;
        String accessToken = "testAccessToken";

        // Mocking
        given(memberService.getKakaoAccessToken(memberId)).willReturn(accessToken);
        given(talkCalendarClientService.getEventList(accessToken, from, to)).willThrow(
            new IllegalArgumentException("잘못된 To 값 입니다."));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> talkCalendarService.getUpdatedEventList(from, to, memberId));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(), "잘못된 To 값 입니다.");
        verify(memberService).getKakaoAccessToken(any(Long.class));
        verify(talkCalendarClientService).getEventList(accessToken, from, to);
    }

    @Test
    void getUpdatedEventListTest_InvalidEventId() {
        // Given
        Long memberId = 1L;
        String from = "2024-10-01T00:00:00Z";
        String to = "2024-10-31T23:59:59Z";
        EventGetResponse eventGetResponse = new EventGetResponse("testId", "Test Title",
            "USER", null, false, false, null, null,
            "Test Description", null, null, "TestColor", null);
        EventListGetResponse eventListGetResponse = new EventListGetResponse(
            new ArrayList<>(List.of((eventGetResponse))));
        String accessToken = "testAccessToken";
        EventDetailGetResponse eventDetailGetResponseMock = new EventDetailGetResponse(
            eventGetResponse);

        // Mocking
        given(memberService.getKakaoAccessToken(memberId)).willReturn(accessToken);
        given(talkCalendarClientService.getEventList(accessToken, from, to)).willReturn(
            eventListGetResponse);
        given(talkCalendarClientService.getEvent(accessToken, eventGetResponse.id())).willThrow(
            new IllegalArgumentException("존재하지 않는 EventID입니다."));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> talkCalendarService.getUpdatedEventList(from, to, memberId));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(), "존재하지 않는 EventID입니다.");
        verify(memberService).getKakaoAccessToken(any(Long.class));
        verify(talkCalendarClientService).getEventList(accessToken, from, to);
        verify(talkCalendarClientService).getEvent(accessToken, eventGetResponse.id());
    }

    @Test
    void createEventTest_InvalidEventCreateRequest() throws JsonProcessingException {
        Long memberId = 1L;
        TimeCreateRequest timeRequest = new TimeCreateRequest("2024-10-01T00:00:00Z",
            "2024-10-01T03:00:00Z", "Asia/Seoul", false);
        EventCreateRequest eventCreateRequest = null;
        String accessToken = "testAccessToken";
        String eventId = "testEventId";
        String eventJson = objectMapper.writeValueAsString(eventCreateRequest);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("event", eventJson);
        EventCreateResponse responseBody = new EventCreateResponse(eventId);

        // Mocking
        given(memberService.getKakaoAccessToken(memberId)).willReturn(accessToken);
        given(talkCalendarClientService.createEvent(accessToken, formData)).willThrow(
            new EventCreationException("EventCreateRequest 에러"));

        // When
        Exception e = assertThrows(EventCreationException.class,
            () -> talkCalendarService.createEvent(eventCreateRequest, memberId));

        // Then
        assertEquals(e.getClass(), EventCreationException.class);
        assertEquals(e.getMessage(), "EventCreateRequest 에러");
        verify(memberService).getKakaoAccessToken(memberId);
        verify(talkCalendarClientService).createEvent(accessToken, formData);
    }

    @Test
    void createEventTest_InvalidMemberId() {
        // Given
        Long memberId = 1L;
        EventCreateRequest eventCreateRequestMcok = mock(EventCreateRequest.class);

        // Mocking
        given(memberService.getKakaoAccessToken(memberId)).willThrow(
            new IllegalArgumentException("존재하지 않는 MemberID입니다."));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> talkCalendarService.createEvent(eventCreateRequestMcok, memberId));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(), "존재하지 않는 MemberID입니다.");
        verify(memberService).getKakaoAccessToken(memberId);
    }

}
