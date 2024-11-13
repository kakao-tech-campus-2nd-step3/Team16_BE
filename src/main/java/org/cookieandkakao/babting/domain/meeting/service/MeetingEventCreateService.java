package org.cookieandkakao.babting.domain.meeting.service;

import jakarta.transaction.Transactional;
import java.util.List;
import org.cookieandkakao.babting.domain.calendar.dto.request.EventCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.request.TimeCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponse;
import org.cookieandkakao.babting.domain.calendar.entity.Event;
import org.cookieandkakao.babting.domain.calendar.service.EventService;
import org.cookieandkakao.babting.domain.calendar.service.TalkCalendarService;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingEventCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingTimeCreateRequest;
import org.cookieandkakao.babting.domain.meeting.entity.MeetingEvent;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingEventRepository;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class MeetingEventCreateService {

    private final TalkCalendarService talkCalendarService;
    private final EventService eventService;
    private final MeetingEventRepository meetingEventRepository;

    public MeetingEventCreateService(TalkCalendarService talkCalendarService,
        EventService eventService,
        MeetingEventRepository meetingEventRepository) {
        this.talkCalendarService = talkCalendarService;
        this.eventService = eventService;
        this.meetingEventRepository = meetingEventRepository;
    }

    public EventCreateResponse addMeetingEvent(Long memberId,
        MeetingEventCreateRequest meetingEventCreateRequest) {
        EventCreateRequest eventCreateRequest = EventCreateRequest.from(meetingEventCreateRequest);
        return talkCalendarService.createEvent(eventCreateRequest, memberId);
    }

    @Transactional
    public void saveMeetingAvoidTime(MemberMeeting memberMeeting,
        List<MeetingTimeCreateRequest> avoidTimeCreateRequests) {

        if (avoidTimeCreateRequests.isEmpty() || avoidTimeCreateRequests == null) {
            return;
        }

        List<MeetingEvent> meetingEvents = avoidTimeCreateRequests.stream()
            .map(
                avoidTimeCreateRequest -> createMeetingEvent(memberMeeting, avoidTimeCreateRequest))
            .toList();

        meetingEventRepository.saveAll(meetingEvents);
    }


    private MeetingEvent createMeetingEvent(MemberMeeting memberMeeting,
        MeetingTimeCreateRequest meetingTimeCreateRequest) {

        TimeCreateRequest timeCreateRequest = meetingTimeCreateRequest.toTimeCreateRequest();
        Event avoidEvent = eventService.saveAvoidTimeEvent(timeCreateRequest.toEntity());

        return new MeetingEvent(memberMeeting, avoidEvent);
    }

}