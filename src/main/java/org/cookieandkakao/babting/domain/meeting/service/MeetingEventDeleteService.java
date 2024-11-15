package org.cookieandkakao.babting.domain.meeting.service;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.cookieandkakao.babting.domain.meeting.entity.MeetingEvent;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class MeetingEventDeleteService {

    private final MeetingEventRepository meetingEventRepository;
    private final EntityManager entityManager;

    public MeetingEventDeleteService(MeetingEventRepository meetingEventRepository,
        EntityManager entityManager) {
        this.meetingEventRepository = meetingEventRepository;
        this.entityManager = entityManager;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteMeetingEvent(MemberMeeting memberMeeting)  {
        List<MeetingEvent> existingEvents = meetingEventRepository.findByMemberMeeting(memberMeeting);
        meetingEventRepository.deleteAll(existingEvents);
        entityManager.flush();
    }
}
