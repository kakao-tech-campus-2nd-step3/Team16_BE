package org.cookieandkakao.babting.domain.meeting.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.cookieandkakao.babting.domain.calendar.dto.request.EventCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.request.TimeCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponse;
import org.cookieandkakao.babting.domain.calendar.entity.Event;
import org.cookieandkakao.babting.domain.calendar.entity.Time;
import org.cookieandkakao.babting.domain.calendar.exception.EventCreationException;
import org.cookieandkakao.babting.domain.calendar.service.EventService;
import org.cookieandkakao.babting.domain.calendar.service.TalkCalendarService;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingEventCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingTimeCreateRequest;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingEventRepository;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MeetingEventCreateServiceTest {

    @Mock
    private TalkCalendarService talkCalendarService;
    @Mock
    private EventService eventService;
    @Mock
    private MemberService memberService;
    @Mock
    private MeetingService meetingService;
    @Mock
    private MeetingEventRepository meetingEventRepository;

    @InjectMocks
    private MeetingEventCreateService meetingEventCreateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static final Long VALID_MEMBER_ID = 1L;

    @Nested
    class 캘린더에_일정_추가_테스트 {

        @Test
        void 성공() {
            // Given
            MeetingEventCreateRequest meetingEventCreateRequest = mock(
                MeetingEventCreateRequest.class);
            MeetingTimeCreateRequest meetingTimeCreateRequest = mock(
                MeetingTimeCreateRequest.class);
            TimeCreateRequest timeCreateRequest = mock(TimeCreateRequest.class);
            EventCreateResponse expectedResponse = mock(EventCreateResponse.class);

            // Mocking
            given(meetingEventCreateRequest.title()).willReturn("Meeting Title");
            given(meetingEventCreateRequest.time()).willReturn(meetingTimeCreateRequest);
            given(meetingTimeCreateRequest.toTimeCreateRequest()).willReturn(timeCreateRequest);
            given(
                talkCalendarService.createEvent(any(EventCreateRequest.class), eq(VALID_MEMBER_ID)))
                .willReturn(expectedResponse);

            // When
            EventCreateResponse result = meetingEventCreateService.addMeetingEvent(VALID_MEMBER_ID,
                meetingEventCreateRequest);

            // Then
            assertEquals(expectedResponse, result);
            assertEquals(result.eventId(), expectedResponse.eventId());
            verify(talkCalendarService).createEvent(any(EventCreateRequest.class),
                eq(VALID_MEMBER_ID));
        }

        @Test
        void 실패_EventCreationException예외() {
            // Given
            MeetingEventCreateRequest meetingEventCreateRequest = mock(
                MeetingEventCreateRequest.class);
            MeetingTimeCreateRequest meetingTimeCreateRequest = mock(
                MeetingTimeCreateRequest.class);
            TimeCreateRequest timeCreateRequest = mock(TimeCreateRequest.class);

            // Mocking
            given(meetingEventCreateRequest.title()).willReturn("Meeting Title");
            given(meetingEventCreateRequest.time()).willReturn(meetingTimeCreateRequest);
            given(meetingTimeCreateRequest.toTimeCreateRequest()).willReturn(timeCreateRequest);
            given(
                talkCalendarService.createEvent(any(EventCreateRequest.class), eq(VALID_MEMBER_ID)))
                .willThrow(new EventCreationException("Event 생성 중 오류 발생"));

            // When
            Exception e = assertThrows(EventCreationException.class,
                () -> meetingEventCreateService.addMeetingEvent(VALID_MEMBER_ID,
                    meetingEventCreateRequest));

            // Then
            assertEquals(e.getClass(), EventCreationException.class);
            assertEquals(e.getMessage(), "Event 생성 중 오류 발생");
            verify(talkCalendarService).createEvent(any(EventCreateRequest.class),
                eq(VALID_MEMBER_ID));
        }
    }

    @Nested
    class 피하고_싶은_시간_일정_저장_테스트 {

        @Test
        void 성공_피하고_싶은_시간_없는_경우() {
            // Given
            MemberMeeting memberMeeting = mock(MemberMeeting.class);
            List<MeetingTimeCreateRequest> emptyAvoidTimeRequests = List.of();

            // When & Then
            assertDoesNotThrow(() -> meetingEventCreateService.saveMeetingAvoidTime(memberMeeting,
                emptyAvoidTimeRequests));
        }

        @Test
        void 성공_피하고_싶은_시간_있는_경우() {
            // Given
            MeetingTimeCreateRequest avoidTimeCreateRequest = mock(MeetingTimeCreateRequest.class);
            TimeCreateRequest timeCreateRequest = mock(TimeCreateRequest.class);
            Time time = mock(Time.class);
            Event avoidEvent = mock(Event.class);
            List<MeetingTimeCreateRequest> avoidTimeCreateRequests = List.of(
                avoidTimeCreateRequest);
            MemberMeeting memberMeeting = mock(MemberMeeting.class);

            // Mocking
            given(avoidTimeCreateRequest.toTimeCreateRequest()).willReturn(timeCreateRequest);
            given(timeCreateRequest.toEntity()).willReturn(time);
            given(eventService.saveAvoidTimeEvent(time)).willReturn(avoidEvent);

            // When
            assertDoesNotThrow(() -> meetingEventCreateService.saveMeetingAvoidTime(memberMeeting,
                avoidTimeCreateRequests));

            // Then
            verify(eventService).saveAvoidTimeEvent(time);
            verify(meetingEventRepository).saveAll(anyList());
        }

        @Test
        void 실패_일정_생성_중_예외() {
            // Given
            MemberMeeting memberMeeting = mock(MemberMeeting.class);
            MeetingTimeCreateRequest avoidTimeCreateRequest = mock(MeetingTimeCreateRequest.class);
            TimeCreateRequest timeCreateRequest = mock(TimeCreateRequest.class);
            Time time = mock(Time.class);
            List<MeetingTimeCreateRequest> avoidTimeCreateRequests = List.of(
                avoidTimeCreateRequest);

            // Mocking
            given(avoidTimeCreateRequest.toTimeCreateRequest()).willReturn(timeCreateRequest);
            given(timeCreateRequest.toEntity()).willReturn(time);
            given(eventService.saveAvoidTimeEvent(time)).willThrow(
                new IllegalArgumentException("MeetingEvent 생성 중 오류 발생"));

            // When
            Exception e = assertThrows(IllegalArgumentException.class,
                () -> meetingEventCreateService.saveMeetingAvoidTime(memberMeeting,
                    avoidTimeCreateRequests));

            // Then
            assertEquals(e.getClass(), IllegalArgumentException.class);
            assertEquals(e.getMessage(), "MeetingEvent 생성 중 오류 발생");
        }
    }
}