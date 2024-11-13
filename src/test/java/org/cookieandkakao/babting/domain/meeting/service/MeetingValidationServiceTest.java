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
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingAlreadyConfirmedException;
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingHostUnauthorizedException;
import org.cookieandkakao.babting.domain.meeting.exception.membermeeting.MemberMeetingNotFoundException;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

    @Nested
    class 호스트_권한_검증_테스트 {

        @Test
        void 성공() {
            // Given
            Long kakaoMemberId = 1L;
            Member member = new Member(kakaoMemberId);
            Meeting meeting = new Meeting(null, "Test Meeting", LocalDateTime.now().toLocalDate(),
                LocalDateTime.now().toLocalDate(), 2, LocalDateTime.now().toLocalTime(),
                LocalDateTime.now().toLocalTime());
            MemberMeeting nonHostMemberMeeting = new MemberMeeting(member, meeting, true);

            // Mocking
            given(meetingService.findMemberMeeting(member, meeting)).willReturn(
                nonHostMemberMeeting);

            // When & Then
            assertDoesNotThrow(
                () -> meetingValidationService.validateHostPermission(member, meeting));
        }

        @Test
        void 실패_호스트가_아닌_경우() {
            // Given
            Long kakaoMemberId = 1L;
            Member member = new Member(kakaoMemberId);
            Meeting meeting = new Meeting(null, "Test Meeting", LocalDateTime.now().toLocalDate(),
                LocalDateTime.now().toLocalDate(), 2, LocalDateTime.now().toLocalTime(),
                LocalDateTime.now().toLocalTime());
            MemberMeeting nonHostMemberMeeting = new MemberMeeting(member, meeting, false);

            // Mocking
            given(meetingService.findMemberMeeting(member, meeting)).willReturn(
                nonHostMemberMeeting);

            // When
            Exception e = assertThrows(MeetingHostUnauthorizedException.class,
                () -> meetingValidationService.validateHostPermission(member, meeting));

            // Then
            assertEquals(e.getClass(), MeetingHostUnauthorizedException.class);
            assertEquals(e.getMessage(), "권한이 없습니다.");
            verify(meetingService).findMemberMeeting(member, meeting);
        }

        @Test
        void 실패_해당_모임에_멤버가_없는_경우() {
            // Given
            Long kakaoMemberId = 1L;
            Member member = new Member(kakaoMemberId);
            Meeting meeting = new Meeting(null, "Test Meeting", LocalDateTime.now().toLocalDate(),
                LocalDateTime.now().toLocalDate(), 2, LocalDateTime.now().toLocalTime(),
                LocalDateTime.now().toLocalTime());

            // Mocking
            given(meetingService.findMemberMeeting(member, meeting))
                .willThrow(new MemberMeetingNotFoundException("해당 모임에 회원이 존재하지 않습니다."));

            // When
            Exception e = assertThrows(MemberMeetingNotFoundException.class,
                () -> meetingValidationService.validateHostPermission(member, meeting));

            // Then
            assertEquals(e.getClass(), MemberMeetingNotFoundException.class);
            assertEquals(e.getMessage(), "해당 모임에 회원이 존재하지 않습니다.");
            verify(meetingService).findMemberMeeting(member, meeting);
        }
    }

    @Nested
    class 모임_확정_시간_검증_테스트 {

        @Test
        void 성공_모임에_확정_시간_없는_경우() {
            // Given
            Meeting meeting = mock(Meeting.class);

            // Mocking
            given(meeting.getConfirmDateTime()).willReturn(null);

            // When & Then
            assertDoesNotThrow(() -> meetingValidationService.validateMeetingConfirmation(meeting));
        }

        @Test
        void 실패_모임에_확정_시간_있는_경우() {
            // Given
            LocalDateTime testTime = LocalDateTime.of(2024, 11, 6, 12, 30, 35);
            Meeting meeting = mock(Meeting.class);

            // Mocking
            given(meeting.getConfirmDateTime()).willReturn(testTime);

            // When
            Exception e = assertThrows(MeetingAlreadyConfirmedException.class,
                () -> meetingValidationService.validateMeetingConfirmation(meeting));

            // Then
            assertEquals(e.getClass(), MeetingAlreadyConfirmedException.class);
            assertEquals(e.getMessage(), "이미 모임 시간이 확정되었습니다.");
            verify(meeting).getConfirmDateTime();
        }
    }
}