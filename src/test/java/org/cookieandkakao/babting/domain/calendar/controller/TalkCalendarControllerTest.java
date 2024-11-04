package org.cookieandkakao.babting.domain.calendar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.SuccessBody;
import org.cookieandkakao.babting.domain.calendar.dto.request.EventCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.request.TimeCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventDetailGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponse;
import org.cookieandkakao.babting.domain.calendar.service.EventService;
import org.cookieandkakao.babting.domain.calendar.service.TalkCalendarService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TalkCalendarControllerTest {

    @Mock
    private TalkCalendarService talkCalendarService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private TalkCalendarController talkCalendarController;

    private static final Long MEMBER_ID = 1L;

    @Test
    void getEventListTest_HasEvent() {
        // Given
        String from = "test From";
        String to = "test To";
        EventGetResponse eventGetResponse = new EventGetResponse("testId", "Test Title",
            "USER", null, false, false, null, null,
            "Test Description", null, null, "TestColor", null);
        List<EventGetResponse> eventGetResponseList = List.of(eventGetResponse);
        EventListGetResponse eventListGetResponse = new EventListGetResponse(eventGetResponseList);

        // Mocking
        given(talkCalendarService.getUpdatedEventList(from, to, MEMBER_ID)).willReturn(
            eventGetResponseList);

        // When
        ResponseEntity<SuccessBody<EventListGetResponse>> result = talkCalendarController.getEventList(
            from, to, MEMBER_ID);

        // Then
        verify(talkCalendarService).getUpdatedEventList(any(), any(), any());
        assert result.getBody() != null;
        assert result.getStatusCode() == HttpStatus.OK;
        assert result.getBody().getData().equals(eventListGetResponse);
        assert result.getBody().getData().events().size() == 1;
        assert result.getBody().getData().events().getFirst().id().equals("testId");
        assert result.getBody().getData().events().getFirst().title().equals("Test Title");
        assert result.getBody().getMessage().equals("일정 목록을 조회했습니다.");
    }

    @Test
    void getEventListTest_NoEvent() {
        // Given
        String from = "test From";
        String to = "test To";

        // Mocking
        given(talkCalendarService.getUpdatedEventList(from, to, MEMBER_ID)).willReturn(
            Collections.emptyList());

        // When
        ResponseEntity<SuccessBody<EventListGetResponse>> result = talkCalendarController.getEventList(
            from, to, MEMBER_ID);

        // Then
        verify(talkCalendarService).getUpdatedEventList(any(), any(), any());
        assert result.getBody() != null;
        assert result.getStatusCode() == HttpStatus.OK;
        assert result.getBody().getData().events().equals(Collections.emptyList());
        assert result.getBody().getData().events().isEmpty();
        assert result.getBody().getMessage().equals("조회된 일정 목록이 없습니다.");
    }

    @Test
    void getEventTest_HasEvent() {
        // Given
        String eventId = "testId";
        EventGetResponse eventGetResponse = new EventGetResponse("testId", "Test Title",
            "USER", null, false, false, null, null,
            "Test Description", null, null, "TestColor", null);
        EventDetailGetResponse eventDetailGetResponse = new EventDetailGetResponse(
            eventGetResponse);

        // Mocking
        given(talkCalendarService.getEvent(MEMBER_ID, eventId)).willReturn(eventDetailGetResponse);

        // When
        ResponseEntity<SuccessBody<EventDetailGetResponse>> result = talkCalendarController.getEvent(
            eventId, MEMBER_ID);

        // Then
        verify(talkCalendarService).getEvent(MEMBER_ID, eventId);
        assert result.getBody() != null;
        assert result.getStatusCode() == HttpStatus.OK;
        assert result.getBody().getData().equals(eventDetailGetResponse);
        assert result.getBody().getData().event().equals(eventGetResponse);
        assert result.getBody().getData().event().id().equals("testId");
        assert result.getBody().getData().event().title().equals("Test Title");
        assert result.getBody().getMessage().equals("일정을 조회했습니다.");
    }

    @Test
    void getEventTest_NoEvent() {
        // Given
        String eventId = "testId";

        // Mocking
        given(talkCalendarService.getEvent(MEMBER_ID, eventId)).willReturn(null);

        // When
        ResponseEntity<SuccessBody<EventDetailGetResponse>> result = talkCalendarController.getEvent(
            eventId, MEMBER_ID);

        // Then
        verify(talkCalendarService).getEvent(MEMBER_ID, eventId);
        assert result.getBody() != null;
        assert result.getStatusCode() == HttpStatus.OK;
        ;
        assert result.getBody().getData() == null;
        assert result.getBody().getMessage().equals("조회된 일정이 없습니다.");
    }

    @Test
    void createEventTest() {
        // Given
        TimeCreateRequest timeRequest = new TimeCreateRequest("2024-10-01T00:00:00Z",
            "2024-10-01T03:00:00Z", "Asia/Seoul", false);
        EventCreateRequest eventCreateRequest = new EventCreateRequest("testTitle", timeRequest,
            null, List.of(10), null);
        EventCreateResponse eventCreateResponse = new EventCreateResponse("testId");

        // Mocking
        given(talkCalendarService.createEvent(eventCreateRequest, MEMBER_ID)).willReturn(
            eventCreateResponse);

        // When
        ResponseEntity<SuccessBody<EventCreateResponse>> result = talkCalendarController.createEvent(
            eventCreateRequest, MEMBER_ID);

        // Then
        verify(talkCalendarService).createEvent(eventCreateRequest, MEMBER_ID);
        assert result.getBody() != null;
        assert result.getStatusCode() == HttpStatus.CREATED;
        assert result.getBody().getData().equals(eventCreateResponse);
        assert result.getBody().getMessage().equals("일정이 성공적으로 생성되었습니다.");
    }

}