package org.cookieandkakao.babting.domain.meeting.service;

import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingAlreadyConfirmedException;
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingHostUnauthorizedException;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.springframework.stereotype.Service;

@Service
public class MeetingValidationService {

    private final MeetingService meetingService;

    public MeetingValidationService(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    public void validateHostPermission(Member member, Meeting meeting) {
        MemberMeeting memberMeeting = meetingService.findMemberMeeting(member, meeting);
        if (!memberMeeting.isHost()) {
            throw new MeetingHostUnauthorizedException("권한이 없습니다.");
        }
    }

    public void validateMeetingConfirmation(Meeting meeting) {
        if (meeting.getConfirmDateTime() != null) {
            throw new MeetingAlreadyConfirmedException("이미 모임 시간이 확정되었습니다.");
        }
    }

}
