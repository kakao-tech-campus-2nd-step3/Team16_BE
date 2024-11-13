package org.cookieandkakao.babting.domain.meeting.service.meetingservice;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingUpdateRequest;
import org.cookieandkakao.babting.domain.meeting.entity.Location;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.junit.jupiter.api.Test;

class MeetingUpdateTest extends MeetingServiceTest {

    @Test
    void 모임_수정_성공() {
        // given
        Member member = mock(Member.class);
        Location location = mock(Location.class);

        MeetingUpdateRequest meetingUpdateRequest = new MeetingUpdateRequest(baseLocation, "북대", LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2), 3, LocalTime.of(14, 0), LocalTime.of(17, 0));

        Meeting meeting = new Meeting(location, "전대", LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2), 3, LocalTime.of(14, 0), LocalTime.of(17, 0));

        MemberMeeting memberMeeting = new MemberMeeting(member, meeting, true);

        // 기존 모임을 조회할 수 있도록 설정
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(memberService.findMember(1L)).thenReturn(member);
        when(memberMeetingRepository.findByMemberAndMeeting(member, meeting)).thenReturn(Optional.of(memberMeeting));


        // when
        meetingService.updateMeeting(1L, 1L, meetingUpdateRequest);

        // then
        assertEquals("북대", meeting.getTitle());

    }
}