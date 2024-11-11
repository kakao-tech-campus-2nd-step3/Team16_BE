package org.cookieandkakao.babting.domain.meeting.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MeetingValidationServiceTest {

    @InjectMocks
    private MeetingValidationService meetingValidationService;

    @Mock
    private MeetingService meetingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateHostPermissionTest() {
        // Given
        Long kakaoMemberId = 1L;
        Member member = new Member(kakaoMemberId);
        Meeting meeting = new Meeting(null, "Test Meeting", LocalDateTime.now().toLocalDate(),
            LocalDateTime.now().toLocalDate(), 2, LocalDateTime.now().toLocalTime(),
            LocalDateTime.now().toLocalTime());
        MemberMeeting nonHostMemberMeeting = new MemberMeeting(member, meeting, true);

        // Mocking
        given(meetingService.findMemberMeeting(member, meeting)).willReturn(nonHostMemberMeeting);

        // When & Then
        assertDoesNotThrow(() -> meetingValidationService.validateHostPermission(member, meeting));

    }

    @Test
    void validateHostPermissionTest_NotHost() {
        // Given
        Long kakaoMemberId = 1L;
        Member member = new Member(kakaoMemberId);
        Meeting meeting = new Meeting(null, "Test Meeting", LocalDateTime.now().toLocalDate(),
            LocalDateTime.now().toLocalDate(), 2, LocalDateTime.now().toLocalTime(),
            LocalDateTime.now().toLocalTime());
        MemberMeeting nonHostMemberMeeting = new MemberMeeting(member, meeting, false);

        // Mocking
        given(meetingService.findMemberMeeting(member, meeting)).willReturn(nonHostMemberMeeting);

        // When
        Exception e = assertThrows(IllegalStateException.class,
            () -> meetingValidationService.validateHostPermission(member, meeting));

        // Then
        assertEquals(e.getClass(), IllegalStateException.class);
        assertEquals(e.getMessage(), "권한이 없습니다.");
        verify(meetingService).findMemberMeeting(member, meeting);
    }

    @Test
    void validateMeetingConfirmationTest() {
        // Given
        Meeting meeting = mock(Meeting.class);

        // Mocking
        given(meeting.getConfirmDateTime()).willReturn(null);

        // When & Then
        assertDoesNotThrow(() -> meetingValidationService.validateMeetingConfirmation(meeting));

    }

    @Test
    void validateMeetingConfirmationTest_ExistingConfirmDateTime() {
        LocalDateTime testTime = LocalDateTime.of(2024,11,6,12,30,35);
        Meeting meeting = mock(Meeting.class);

        // Mocking
        given(meeting.getConfirmDateTime()).willReturn(testTime);

        // When
        Exception e = assertThrows(IllegalStateException.class,
            () -> meetingValidationService.validateMeetingConfirmation(meeting));

        // Then
        assertEquals(e.getClass(), IllegalStateException.class);
        assertEquals(e.getMessage(), "이미 모임 시간이 확정되었습니다.");
        verify(meeting).getConfirmDateTime();
    }
}