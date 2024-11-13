package org.cookieandkakao.babting.domain.calendar.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.SuccessBody;
import org.cookieandkakao.babting.common.exception.customexception.ApiException;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventDetailGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponse;
import org.cookieandkakao.babting.domain.calendar.exception.EventDetailNotFoundException;
import org.cookieandkakao.babting.domain.calendar.service.EventService;
import org.cookieandkakao.babting.domain.calendar.service.TalkCalendarService;
import org.junit.jupiter.api.Nested;
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

    @Nested
    class 일정_목록_조회_테스트 {

        @Test
        void 성공_일정_목록_있는_경우() {
            // Given
            String from = "test From";
            String to = "test To";
            EventGetResponse eventGetResponse = new EventGetResponse("testId", "Test Title",
                "USER", null, false, false, null, null,
                "Test Description", null, null, "TestColor", null);
            List<EventGetResponse> eventGetResponseList = List.of(eventGetResponse);
            EventListGetResponse eventListGetResponse = new EventListGetResponse(
                eventGetResponseList);

            // Mocking
            given(talkCalendarService.getUpdatedEventList(from, to, MEMBER_ID)).willReturn(
                eventGetResponseList);

            // When
            ResponseEntity<SuccessBody<EventListGetResponse>> result = talkCalendarController.getEventList(
                from, to, MEMBER_ID);

            // Then
            verify(talkCalendarService).getUpdatedEventList(any(), any(), any());
            assertNotNull(result.getBody());
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody().getData()).isEqualTo(eventListGetResponse);
            assertThat(result.getBody().getData().events().size()).isEqualTo(1);
            assertThat(result.getBody().getData().events().getFirst()).isEqualTo(eventGetResponse);
            assertThat(result.getBody().getData().events().getFirst().id()).isEqualTo("testId");
            assertThat(result.getBody().getData().events().getFirst().title()).isEqualTo(
                "Test Title");
            assertThat(result.getBody().getMessage()).isEqualTo("일정 목록을 조회했습니다.");
        }

        @Test
        void 성공_일정_목록_없는_경우() {
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
            assertNotNull(result.getBody());
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertTrue(result.getBody().getData().events().isEmpty());
            assertThat(result.getBody().getMessage()).isEqualTo("조회된 일정 목록이 없습니다.");
        }

        @Test
        void 실패_ApiException에외() {
            // Given
            String from = "2024-01-01T00:00:00Z";
            String to = "2024-01-31T00:00:00Z";

            // Mocking
            given(talkCalendarService.getUpdatedEventList(from, to, MEMBER_ID)).willThrow(
                new ApiException("API 호출 중 오류 발생"));

            // When
            Exception e = assertThrows(ApiException.class,
                () -> talkCalendarController.getEventList(from, to, MEMBER_ID));

            // Then
            assertThat(e.getClass()).isEqualTo(ApiException.class);
            assertThat(e.getMessage()).isEqualTo("API 호출 중 오류 발생");
        }
    }

    @Nested
    class 일정_상세_조회_테스트 {

        @Test
        void 성공_일정_있는_경우() {
            // Given
            String eventId = "testId";
            EventGetResponse eventGetResponse = new EventGetResponse("testId", "Test Title",
                "USER", null, false, false, null, null,
                "Test Description", null, null, "TestColor", null);
            EventDetailGetResponse eventDetailGetResponse = new EventDetailGetResponse(
                eventGetResponse);

            // Mocking
            given(talkCalendarService.getEvent(MEMBER_ID, eventId)).willReturn(
                eventDetailGetResponse);

            // When
            ResponseEntity<SuccessBody<EventDetailGetResponse>> result = talkCalendarController.getEvent(
                eventId, MEMBER_ID);

            // Then
            verify(talkCalendarService).getEvent(MEMBER_ID, eventId);
            assertNotNull(result.getBody());
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody().getData()).isEqualTo(eventDetailGetResponse);
            assertThat(result.getBody().getData().event()).isEqualTo(eventGetResponse);
            assertThat(result.getBody().getData().event().id()).isEqualTo("testId");
            assertThat(result.getBody().getData().event().title()).isEqualTo("Test Title");
            assertThat(result.getBody().getMessage()).isEqualTo("일정을 조회했습니다.");
        }

        @Test
        void 실패_일정_없는_경우() {
            // Given
            String eventId = "testId";

            // Mocking
            given(talkCalendarService.getEvent(MEMBER_ID, eventId)).willThrow(
                new EventDetailNotFoundException("요청한 일정ID에 해당하는 일정을 찾을 수 없습니다."));

            // When
            Exception e = assertThrows(EventDetailNotFoundException.class,
                () -> talkCalendarController.getEvent(eventId, MEMBER_ID));

            // Then
            verify(talkCalendarService).getEvent(MEMBER_ID, eventId);
            assertThat(e.getClass()).isEqualTo(EventDetailNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("요청한 일정ID에 해당하는 일정을 찾을 수 없습니다.");
        }

        @Test
        void 실패_ApiException에외() {
            // Given
            String eventId = "testId";

            // Mocking
            given(talkCalendarService.getEvent(MEMBER_ID, eventId)).willThrow(
                new ApiException("API 호출 중 오류 발생"));

            // When
            Exception e = assertThrows(ApiException.class,
                () -> talkCalendarController.getEvent(eventId, MEMBER_ID));

            // Then
            assertThat(e.getClass()).isEqualTo(ApiException.class);
            assertThat(e.getMessage()).isEqualTo("API 호출 중 오류 발생");
        }
    }
}