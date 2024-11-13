package org.cookieandkakao.babting.domain.meeting.service.meetingservice;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.exception.membermeeting.MemberMeetingNotFoundException;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.junit.jupiter.api.Test;

class MeetingExitTest extends MeetingServiceTest {

    @Test
    void 모임장_탈퇴_성공() {
        // given
        Member member = mock(Member.class);
        Meeting meeting = mock(Meeting.class);
        MemberMeeting memberMeeting = new MemberMeeting(member, meeting, true);

        when(memberService.findMember(1L)).thenReturn(member);
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(memberMeetingRepository.findByMemberAndMeeting(member, meeting)).thenReturn(Optional.of(memberMeeting));

        // when
        meetingService.exitMeeting(1L, 1L);

        // then
        verify(memberMeetingRepository).deleteAllByMeeting(meeting);
        verify(meetingRepository).delete(meeting);
    }
    @Test
    void 모임_참여자_탈퇴_성공(){
        // given
        Member joiner = mock(Member.class);
        Meeting meeting = mock(Meeting.class);
        MemberMeeting joinerMemberMeeting = new MemberMeeting(joiner, meeting, false);

        when(memberService.findMember(1L)).thenReturn(joiner);
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(memberMeetingRepository.findByMemberAndMeeting(joiner, meeting)).thenReturn(Optional.of(joinerMemberMeeting));
        // when
        meetingService.exitMeeting(1L, 1L);

        // then
        verify(memberMeetingRepository, never()).deleteAllByMeeting(meeting);
        verify(meetingRepository, never()).delete(meeting);
        verify(memberMeetingRepository).delete(joinerMemberMeeting);
    }
    @Test
    void 가입하지_않은_모임_탈퇴_불가(){
        // given
        Member joiner = mock(Member.class);
        Meeting meeting = mock(Meeting.class);
        MemberMeeting joinerMemberMeeting = new MemberMeeting(joiner, meeting, false);

        when(memberService.findMember(1L)).thenReturn(joiner);
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        // when then
        when(memberMeetingRepository.findByMemberAndMeeting(joiner, meeting)).thenReturn(Optional.empty());
        assertThrows(MemberMeetingNotFoundException.class, () -> meetingService.exitMeeting(1L, 1L));

        verify(memberMeetingRepository, never()).deleteAllByMeeting(meeting);
        verify(meetingRepository, never()).delete(meeting);
        verify(memberMeetingRepository, never()).delete(joinerMemberMeeting);
    }
}