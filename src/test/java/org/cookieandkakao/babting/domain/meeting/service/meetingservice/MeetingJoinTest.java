package org.cookieandkakao.babting.domain.meeting.service.meetingservice;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingJoinCreateRequest;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.junit.jupiter.api.Test;

class MeetingJoinTest extends MeetingServiceTest {

    @Test
    void 모임_참가_성공() {
        //given
        Member joiner = mock(Member.class);
        Meeting meeting = mock(Meeting.class);
        MeetingJoinCreateRequest meetingJoinCreateRequest = mock(MeetingJoinCreateRequest.class);

        when(memberService.findMember(1L)).thenReturn(joiner);
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));

        //when
        meetingService.joinMeeting(1L, 1L, meetingJoinCreateRequest);

        //then
        verify(memberMeetingRepository).save(any(MemberMeeting.class));
    }
}