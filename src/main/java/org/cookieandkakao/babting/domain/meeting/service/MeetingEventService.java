package org.cookieandkakao.babting.domain.meeting.service;

import jakarta.transaction.Transactional;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.cookieandkakao.babting.domain.calendar.dto.response.TimeGetResponse;
import org.cookieandkakao.babting.domain.food.service.FoodRepositoryService;
import org.cookieandkakao.babting.domain.meeting.dto.request.ConfirmMeetingGetRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingEventCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingTimeCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.response.MeetingPersonalEventGetResponse;
import org.cookieandkakao.babting.domain.meeting.dto.response.TimeAvailableGetResponse;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MeetingEvent;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.entity.TimeZone;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingEventRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.stereotype.Service;


@Transactional
@Service
public class MeetingEventService {

    private final MemberService memberService;
    private final MeetingService meetingService;
    private final MeetingValidationService meetingValidationService;
    private final MeetingEventCreateService meetingEventCreateService;
    private final FoodRepositoryService foodRepositoryService;

    private final MeetingEventRepository meetingEventRepository;
    private final MeetingTimeCalculationService meetingTimeCalculationService;
    private static final List<Integer> DEFAULT_REMINDER_TIMES = List.of(15, 30);

    public MeetingEventService(MemberService memberService,
        MeetingService meetingService, MeetingValidationService meetingValidationService,
        MeetingEventCreateService meetingEventCreateService,
        FoodRepositoryService foodRepositoryService, MeetingEventRepository meetingEventRepository,
        MeetingTimeCalculationService meetingTimeCalculationService) {
        this.memberService = memberService;
        this.meetingService = meetingService;
        this.meetingValidationService = meetingValidationService;
        this.meetingEventCreateService = meetingEventCreateService;
        this.foodRepositoryService = foodRepositoryService;
        this.meetingEventRepository = meetingEventRepository;
        this.meetingTimeCalculationService = meetingTimeCalculationService;
    }

    public void confirmMeeting(Long memberId, Long meetingId,
        ConfirmMeetingGetRequest confirmMeetingGetRequest) {
        Member member = memberService.findMember(memberId);
        Meeting meeting = meetingService.findMeeting(meetingId);
        Long foodId = confirmMeetingGetRequest.confirmFoodId();

        meetingValidationService.validateHostPermission(member, meeting);
        meetingValidationService.validateMeetingConfirmation(meeting);

        meeting.confirmDateTime(confirmMeetingGetRequest.confirmDateTime());
        meeting.confirmFood(foodRepositoryService.findFoodById(foodId));

        MeetingTimeCreateRequest meetingTimeCreateRequest = createMeetingTimeRequest(meeting);
        MeetingEventCreateRequest meetingEventCreateRequest = createMeetingEventRequest(meeting,
            meetingTimeCreateRequest);

        List<Long> memberIds = meetingService.getMemberIdInMeetingId(meetingId);

        for (Long currentMemberId : memberIds) {
            meetingEventCreateService.addMeetingEvent(currentMemberId, meetingEventCreateRequest);
        }
    }

    private MeetingTimeCreateRequest createMeetingTimeRequest(Meeting meeting) {
        ZonedDateTime startDateTime = meeting.getConfirmDateTime()
            .atZone(ZoneId.of(TimeZone.SEOUL.getArea()));
        ZonedDateTime endDateTime = startDateTime.plusMinutes(meeting.getDurationTime());
        boolean allDay = false;
        return new MeetingTimeCreateRequest(startDateTime.toString(), endDateTime.toString(),
            TimeZone.SEOUL.getArea(), allDay);
    }

    private MeetingEventCreateRequest createMeetingEventRequest(Meeting meeting,
        MeetingTimeCreateRequest meetingTimeCreateRequest) {
        return new MeetingEventCreateRequest(meeting.getTitle(), meetingTimeCreateRequest,
            DEFAULT_REMINDER_TIMES);
    }

    public TimeAvailableGetResponse findAvailableTime(Long meetingId) {
        return meetingTimeCalculationService.findAvailableTime(meetingId);
    }

    public MeetingPersonalEventGetResponse findMeetingPersonalEvent(Long meetingId, Long memberId) {
        List<MeetingEvent> meetingEvents = findAllMeetingEvent(meetingId, memberId);
        List<TimeGetResponse> meetingPersonalEventTimes = meetingEvents.stream()
            .map(meetingEvent -> TimeGetResponse.from(meetingEvent.getEvent().getTime())).toList();
        return new MeetingPersonalEventGetResponse(meetingPersonalEventTimes);
    }

    private List<MeetingEvent> findAllMeetingEvent(Long meetingId, Long memberId) {
        Meeting meeting = meetingService.findMeeting(meetingId);
        Member member = memberService.findMember(memberId);
        MemberMeeting memberMeeting = meetingService.findMemberMeeting(member, meeting);
        return meetingEventRepository.findByMemberMeeting(memberMeeting);
    }
}
