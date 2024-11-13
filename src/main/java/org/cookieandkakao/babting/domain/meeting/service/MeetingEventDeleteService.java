package org.cookieandkakao.babting.domain.meeting.service;

import java.util.List;
import org.cookieandkakao.babting.domain.calendar.service.EventService;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MeetingEvent;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingEventRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class MeetingEventDeleteService {

    private final MeetingEventRepository meetingEventRepository;
    private final MemberService memberService;
    private final MeetingService meetingService;

    public MeetingEventDeleteService(MeetingEventRepository meetingEventRepository,
        MemberService memberService, MeetingService meetingService) {
        this.meetingEventRepository = meetingEventRepository;
        this.memberService = memberService;
        this.meetingService = meetingService;
    }

    @Transactional
    public void deleteMeetingEvent(MemberMeeting memberMeeting)  {
        List<MeetingEvent> existingEvents = meetingEventRepository.findByMemberMeeting(memberMeeting);
        meetingEventRepository.deleteAll(existingEvents);
    }
}
