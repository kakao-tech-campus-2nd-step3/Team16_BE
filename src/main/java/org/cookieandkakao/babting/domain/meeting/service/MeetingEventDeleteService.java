package org.cookieandkakao.babting.domain.meeting.service;

import java.util.List;
import org.cookieandkakao.babting.domain.meeting.entity.MeetingEvent;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class MeetingEventDeleteService {

    private final MeetingEventRepository meetingEventRepository;
    public MeetingEventDeleteService(MeetingEventRepository meetingEventRepository) {
        this.meetingEventRepository = meetingEventRepository;
    }

    @Transactional
    public void deleteMeetingEvent(MemberMeeting memberMeeting)  {
        List<MeetingEvent> existingEvents = meetingEventRepository.findByMemberMeeting(memberMeeting);
        meetingEventRepository.deleteAll(existingEvents);
    }
}
