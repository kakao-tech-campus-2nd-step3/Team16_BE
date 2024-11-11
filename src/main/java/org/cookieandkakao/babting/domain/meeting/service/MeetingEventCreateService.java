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
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MeetingEvent;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingEventRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class MeetingEventCreateService {

    private final TalkCalendarService talkCalendarService;
    private final EventService eventService;
    private final MemberService memberService;
    private final MeetingService meetingService;
    private final MeetingEventRepository meetingEventRepository;

    public MeetingEventCreateService(TalkCalendarService talkCalendarService,
        EventService eventService, MemberService memberService,
        MeetingService meetingService,
        MeetingEventRepository meetingEventRepository) {
        this.talkCalendarService = talkCalendarService;
        this.eventService = eventService;
        this.memberService = memberService;
        this.meetingService = meetingService;
        this.meetingEventRepository = meetingEventRepository;
    }

    // 일정 생성 후 캘린더에 일정 추가
    public EventCreateResponse addMeetingEvent(Long memberId,
        MeetingEventCreateRequest meetingEventCreateRequest) {
        EventCreateRequest eventCreateRequest = convertToEventCreateRequest(
            meetingEventCreateRequest);
        return talkCalendarService.createEvent(eventCreateRequest, memberId);
    }

    private EventCreateRequest convertToEventCreateRequest(
        MeetingEventCreateRequest meetingEventCreateRequest) {
        return new EventCreateRequest(
            meetingEventCreateRequest.title(),
            meetingEventCreateRequest.time().toTimeCreateRequest(), null,
            meetingEventCreateRequest.reminders(), null);
    }

    // 모임별 개인적으로 피하고 싶은 시간 저장하기
    @Transactional
    public void saveMeetingAvoidTime(Long memberId, Long meetingId,
        List<MeetingTimeCreateRequest> avoidTimeCreateRequests) {

        // 피하고 싶은 시간 없으면 그냥 종료
        if (avoidTimeCreateRequests.isEmpty() || avoidTimeCreateRequests == null) {
            return;
        }

        Member member = memberService.findMember(memberId);
        Meeting meeting = meetingService.findMeeting(meetingId);
        MemberMeeting memberMeeting = meetingService.findMemberMeeting(member, meeting);

        for (MeetingTimeCreateRequest avoidTimeCreateRequest : avoidTimeCreateRequests) {
            createAndSaveMeetingEvent(memberMeeting, avoidTimeCreateRequest);
        }
    }

    // 피하고 싶은 시간을 기반으로 MeetingEvent 생성 및 저장
    private void createAndSaveMeetingEvent(MemberMeeting memberMeeting,
        MeetingTimeCreateRequest meetingTimeCreateRequest) {
        // 시간 정보를 Event로 생성 후 저장
        TimeCreateRequest timeCreateRequest = meetingTimeCreateRequest.toTimeCreateRequest();
        Event avoidEvent = eventService.saveAvoidTimeEvent(timeCreateRequest.toEntity());

        // MeetingEvent로 저장
        MeetingEvent meetingEvent = new MeetingEvent(memberMeeting, avoidEvent);
        saveMeetingEvent(meetingEvent);
    }

    private void saveMeetingEvent(MeetingEvent meetingEvent) {
        meetingEventRepository.save(meetingEvent);
    }


}