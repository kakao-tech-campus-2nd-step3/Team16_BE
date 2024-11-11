package org.cookieandkakao.babting.domain.meeting.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import org.cookieandkakao.babting.domain.food.service.FoodRepositoryService;
import org.cookieandkakao.babting.domain.meeting.dto.request.ConfirmMeetingGetRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingEventCreateRequest;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MeetingEventServiceTest {

    @Mock
    private MemberService memberService;
    @Mock
    private MeetingService meetingService;
    @Mock
    private MeetingValidationService meetingValidationService;
    @Mock
    private MeetingEventCreateService meetingEventCreateService;
    @Mock
    private FoodRepositoryService foodRepositoryService;

    @InjectMocks
    private MeetingEventService meetingEventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void confirmMeetingTest() {
        // Given
        Long memberId = 1L;
        Long meetingId = 2L;
        Long foodId = 3L;

        Member member = mock(Member.class);
        Meeting meeting = mock(Meeting.class);
        ConfirmMeetingGetRequest confirmMeetingGetRequest = mock(ConfirmMeetingGetRequest.class);

        LocalDateTime confirmDateTime = LocalDateTime.now();

        // Mocking
        given(memberService.findMember(memberId)).willReturn(member);
        given(meetingService.findMeeting(meetingId)).willReturn(meeting);
        given(confirmMeetingGetRequest.confirmFoodId()).willReturn(foodId);
        given(confirmMeetingGetRequest.confirmDateTime()).willReturn(confirmDateTime);
        given(meetingService.getMemberIdInMeetingId(meetingId)).willReturn(List.of(memberId));
        doNothing().when(meeting).confirmDateTime(confirmDateTime);
        given(meeting.getConfirmDateTime()).willReturn(confirmDateTime);
        given(foodRepositoryService.findFoodById(foodId)).willReturn(null);
        doNothing().when(meetingValidationService).validateHostPermission(member, meeting);
        doNothing().when(meetingValidationService).validateMeetingConfirmation(meeting);

        // When & Then
        assertDoesNotThrow(() -> meetingEventService.confirmMeeting(memberId, meetingId, confirmMeetingGetRequest));
        verify(meeting).confirmDateTime(confirmDateTime);
        verify(meeting).confirmFood(any());
        verify(meetingEventCreateService).addMeetingEvent(eq(memberId), any(MeetingEventCreateRequest.class));
    }

    @Test
    void confirmMeeting_HostPermissionValidationFail() {
        // Given
        Long memberId = 1L;
        Long meetingId = 2L;

        Member member = mock(Member.class);
        Meeting meeting = mock(Meeting.class);
        ConfirmMeetingGetRequest confirmMeetingGetRequest = mock(ConfirmMeetingGetRequest.class);

        // Mocking
        given(memberService.findMember(memberId)).willReturn(member);
        given(meetingService.findMeeting(meetingId)).willReturn(meeting);
        doThrow(new IllegalStateException("권한이 없습니다.")).when(meetingValidationService).validateHostPermission(member, meeting);

        // When
        Exception e = assertThrows(IllegalStateException.class,
            () -> meetingEventService.confirmMeeting(memberId, meetingId, confirmMeetingGetRequest));

        // Then
        assertEquals(e.getClass(), IllegalStateException.class);
        assertEquals(e.getMessage(), "권한이 없습니다.");
        verify(meetingValidationService).validateHostPermission(member, meeting);
    }

    @Test
    void confirmMeeting_ConfirmMeetingValidationFail() {
        // Given
        Long memberId = 1L;
        Long meetingId = 2L;

        Member member = mock(Member.class);
        Meeting meeting = mock(Meeting.class);
        ConfirmMeetingGetRequest confirmMeetingGetRequest = mock(ConfirmMeetingGetRequest.class);

        // Mocking
        given(memberService.findMember(memberId)).willReturn(member);
        given(meetingService.findMeeting(meetingId)).willReturn(meeting);
        doThrow(new IllegalStateException("이미 모임 시간이 확정되었습니다.")).when(meetingValidationService).validateMeetingConfirmation(meeting);

        // When
        Exception e = assertThrows(IllegalStateException.class,
            () -> meetingEventService.confirmMeeting(memberId, meetingId, confirmMeetingGetRequest));

        // Then
        assertEquals(e.getClass(), IllegalStateException.class);
        assertEquals(e.getMessage(), "이미 모임 시간이 확정되었습니다.");
        verify(meetingValidationService).validateHostPermission(member, meeting);
        verify(meetingValidationService).validateMeetingConfirmation(meeting);
    }

    @Test
    void confirmMeeting_NotFoundFood() {
        // Given
        Long memberId = 1L;
        Long meetingId = 2L;
        Long foodId = 3L;

        Member member = mock(Member.class);
        Meeting meeting = mock(Meeting.class);
        ConfirmMeetingGetRequest confirmMeetingGetRequest = mock(ConfirmMeetingGetRequest.class);

        // Mocking
        given(memberService.findMember(memberId)).willReturn(member);
        given(meetingService.findMeeting(meetingId)).willReturn(meeting);
        given(confirmMeetingGetRequest.confirmFoodId()).willReturn(foodId);
        doThrow(new IllegalArgumentException("음식을 찾을 수 없습니다.")).when(foodRepositoryService).findFoodById(foodId);

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> meetingEventService.confirmMeeting(memberId, meetingId, confirmMeetingGetRequest));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(), "음식을 찾을 수 없습니다.");
        verify(meetingValidationService).validateHostPermission(member, meeting);
        verify(meetingValidationService).validateMeetingConfirmation(meeting);
        verify(foodRepositoryService).findFoodById(foodId);
    }

}