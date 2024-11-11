package org.cookieandkakao.babting.domain.meeting.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import org.cookieandkakao.babting.domain.calendar.service.EventService;
import org.cookieandkakao.babting.domain.calendar.service.TalkCalendarService;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingEventCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingTimeCreateRequest;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MeetingEvent;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingEventRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
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

    @Test
    void addMeetingEvent() {
        // Given
        Long memberId = 1L;
        MeetingEventCreateRequest meetingEventCreateRequest = mock(MeetingEventCreateRequest.class);
        MeetingTimeCreateRequest meetingTimeCreateRequest = mock(MeetingTimeCreateRequest.class);
        TimeCreateRequest timeCreateRequest = mock(TimeCreateRequest.class);
        EventCreateResponse expectedResponse = mock(EventCreateResponse.class);

        // Mocking
        given(meetingEventCreateRequest.title()).willReturn("Meeting Title");
        given(meetingEventCreateRequest.time()).willReturn(meetingTimeCreateRequest);
        given(meetingTimeCreateRequest.toTimeCreateRequest()).willReturn(timeCreateRequest);
        given(talkCalendarService.createEvent(any(EventCreateRequest.class), eq(memberId)))
            .willReturn(expectedResponse);

        // When
        EventCreateResponse result = meetingEventCreateService.addMeetingEvent(memberId, meetingEventCreateRequest);

        // Then
        assertEquals(expectedResponse, result);
        assertEquals(result.eventId(), expectedResponse.eventId());
        verify(talkCalendarService).createEvent(any(EventCreateRequest.class), eq(memberId));
    }


    @Test
    void saveMeetingAvoidTime_NoAvoidTime() {
        // Given
        Long memberId = 1L;
        Long meetingId = 2L;
        List<MeetingTimeCreateRequest> emptyAvoidTimeRequests = List.of();

        // When & Then
        assertDoesNotThrow(() -> meetingEventCreateService.saveMeetingAvoidTime(memberId, meetingId, emptyAvoidTimeRequests));
    }

    @Test
    void saveMeetingAvoidTime_AvoidTime() {
        // Given
        Long memberId = 1L;
        Long meetingId = 2L;
        MeetingTimeCreateRequest avoidTimeCreateRequest = mock(MeetingTimeCreateRequest.class);
        TimeCreateRequest timeCreateRequest = mock(TimeCreateRequest.class);
        Time time = mock(Time.class);
        Event avoidEvent = mock(Event.class);
        List<MeetingTimeCreateRequest> avoidTimeCreateRequests = List.of(avoidTimeCreateRequest);
        Member member = mock(Member.class);
        Meeting meeting = mock(Meeting.class);
        MemberMeeting memberMeeting = mock(MemberMeeting.class);

        // Mocking
        given(memberService.findMember(memberId)).willReturn(member);
        given(meetingService.findMeeting(meetingId)).willReturn(meeting);
        given(meetingService.findMemberMeeting(member, meeting)).willReturn(memberMeeting);
        given(avoidTimeCreateRequest.toTimeCreateRequest()).willReturn(timeCreateRequest);
        given(timeCreateRequest.toEntity()).willReturn(time);
        given(eventService.saveAvoidTimeEvent(time)).willReturn(avoidEvent);

        // When
        assertDoesNotThrow(() -> meetingEventCreateService.saveMeetingAvoidTime(memberId, meetingId, avoidTimeCreateRequests));

        // Then
        verify(eventService).saveAvoidTimeEvent(time);
        verify(meetingEventRepository).save(any(MeetingEvent.class));
    }

    @Test
    void saveMeetingAvoidTime_InvalidMember() {
        // Given
        Long invalidMemberId = -1L;
        Long meetingId = 2L;
        List<MeetingTimeCreateRequest> avoidTimeCreateRequests = List.of(mock(MeetingTimeCreateRequest.class));

        // Mocking
        given(memberService.findMember(invalidMemberId)).willThrow(new IllegalArgumentException("멤버 ID가 없습니다."));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> meetingEventCreateService.saveMeetingAvoidTime(invalidMemberId, meetingId, avoidTimeCreateRequests));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(),"멤버 ID가 없습니다.");
        verify(memberService).findMember(invalidMemberId);
    }

    @Test
    void saveMeetingAvoidTime_InvalidMeeting() {
        // Given
        Long memberId = 1L;
        Long invalidMeetingId = -2L;
        List<MeetingTimeCreateRequest> avoidTimeCreateRequests = List.of(mock(MeetingTimeCreateRequest.class));
        Member member = mock(Member.class);

        // Mocking
        given(memberService.findMember(memberId)).willReturn(member);
        given(meetingService.findMeeting(invalidMeetingId)).willThrow(new IllegalArgumentException("모임 ID가 없습니다."));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> meetingEventCreateService.saveMeetingAvoidTime(memberId, invalidMeetingId, avoidTimeCreateRequests));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(),"모임 ID가 없습니다.");
        verify(memberService).findMember(memberId);
        verify(meetingService).findMeeting(invalidMeetingId);
    }
}