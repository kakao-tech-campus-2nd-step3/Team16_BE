package org.cookieandkakao.babting.domain.meeting.service.meetingservice;

import static org.mockito.Mockito.mock;

import java.util.Optional;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.when;


class MeetingHostCheckTest extends MeetingServiceTest {

    @Test
    void 모임_주최자_확인() {
        // given
        Member member = mock(Member.class);
        Meeting meeting = mock(Meeting.class);
        MemberMeeting memberMeeting = new MemberMeeting(member, meeting, true);

        when(memberService.findMember(1L)).thenReturn(member);
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(memberMeetingRepository.findByMemberAndMeeting(member, meeting)).thenReturn(Optional.of(memberMeeting));

        // when
        meetingService.checkHost(1L, 1L);

        // then
        Assertions.assertTrue(memberMeeting.isHost());
    }
}
