package org.cookieandkakao.babting.domain.meeting.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.List;
import org.cookieandkakao.babting.domain.food.dto.PersonalPreferenceUpdateRequest;
import org.cookieandkakao.babting.domain.food.service.MeetingFoodPreferenceUpdater;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingTimeCreateRequest;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingNotFoundException;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.exception.MemberNotFoundException;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MeetingPersonalInfoUpdateServiceTest {

    @Mock
    private MeetingFoodPreferenceUpdater meetingFoodPreferenceUpdater;

    @Mock
    private MeetingEventDeleteService meetingEventDeleteService;

    @Mock
    private MeetingEventCreateService meetingEventCreateService;

    @Mock
    private MeetingService meetingService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MeetingPersonalInfoUpdateService meetingPersonalInfoUpdateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class 모임별_개인_정보_업데이트_테스트 {

        @Test
        void 성공() {
            // Given
            Long meetingId = 1L;
            Long memberId = 1L;

            Member member = new Member(1L);
            Meeting meeting = new Meeting(null, "Meeting Title", null, null, 60, null, null);
            MemberMeeting memberMeeting = new MemberMeeting(member, meeting, false);

            List<Long> preferences = List.of(1L, 2L);
            List<Long> nonPreferences = List.of(3L, 4L);
            List<MeetingTimeCreateRequest> times = List.of(
                new MeetingTimeCreateRequest("2024-11-15T10:00", "2024-11-15T11:00", "Asia/Seoul", false)
            );

            PersonalPreferenceUpdateRequest personalPreferenceUpdateRequest = new PersonalPreferenceUpdateRequest(
                preferences,
                nonPreferences,
                times
            );

            // Mocking
            given(memberService.findMember(memberId)).willReturn(member);
            given(meetingService.findMeeting(meetingId)).willReturn(meeting);
            given(meetingService.findMemberMeeting(member, meeting)).willReturn(memberMeeting);

            // When
            meetingPersonalInfoUpdateService.updateMeetingPersonalInfo(meetingId, memberId, personalPreferenceUpdateRequest);

            // Then
            verify(memberService, times(1)).findMember(memberId);
            verify(meetingService, times(1)).findMeeting(meetingId);
            verify(meetingService, times(1)).findMemberMeeting(member, meeting);
            verify(meetingEventDeleteService, times(1)).deleteMeetingEvent(memberMeeting);
            verify(meetingFoodPreferenceUpdater, times(1)).updatePreferences(
                meetingId,
                memberId,
                preferences,
                nonPreferences
            );
            verify(meetingEventCreateService, times(1)).saveMeetingAvoidTime(memberMeeting, times);
        }

        @Test
        void 실패_멤버_찾을_수_없는_경우() {
            // Given
            Long meetingId = 1L;
            Long memberId = 1L;

            PersonalPreferenceUpdateRequest request = new PersonalPreferenceUpdateRequest(
                List.of(1L, 2L), List.of(3L, 4L), List.of(new MeetingTimeCreateRequest("2024-11-15T10:00", "2024-11-15T11:00", "Asia/Seoul", false))
            );

            // Mocking
            given(memberService.findMember(memberId)).willThrow(new MemberNotFoundException("해당 사용자가 존재하지 않습니다."));

            // When
            Exception e = assertThrows(MemberNotFoundException.class, () ->
                meetingPersonalInfoUpdateService.updateMeetingPersonalInfo(meetingId, memberId, request)
            );

            // Then
            assertEquals(e.getClass(), MemberNotFoundException.class);
            assertEquals(e.getMessage(), "해당 사용자가 존재하지 않습니다.");
            verify(memberService, times(1)).findMember(memberId);
        }

        @Test
        void 실패_모임_찾을_수_없는_경우() {
            Long meetingId = 1L;
            Long memberId = 1L;

            Member member = new Member(1L);
            PersonalPreferenceUpdateRequest request = new PersonalPreferenceUpdateRequest(
                List.of(1L, 2L), List.of(3L, 4L), List.of(new MeetingTimeCreateRequest("2024-11-15T10:00", "2024-11-15T11:00", "Asia/Seoul", false))
            );

            // Mocking
            given(memberService.findMember(memberId)).willReturn(member);
            given(meetingService.findMeeting(meetingId)).willThrow(new MeetingNotFoundException("해당 모임이 존재하지 않습니다."));

            // When
            Exception e = assertThrows(MeetingNotFoundException.class, () ->
                meetingPersonalInfoUpdateService.updateMeetingPersonalInfo(meetingId, memberId, request)
            );

            // Then
            assertEquals(e.getClass(), MeetingNotFoundException.class);
            assertEquals(e.getMessage(), "해당 모임이 존재하지 않습니다.");
            verify(memberService, times(1)).findMember(memberId);
            verify(meetingService, times(1)).findMeeting(meetingId);
        }
    }
}
