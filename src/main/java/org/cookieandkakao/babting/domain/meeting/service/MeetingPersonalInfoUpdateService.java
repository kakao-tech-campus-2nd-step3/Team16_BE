package org.cookieandkakao.babting.domain.meeting.service;

import org.cookieandkakao.babting.domain.food.dto.PersonalPreferenceUpdateRequest;
import org.cookieandkakao.babting.domain.food.service.MeetingFoodPreferenceUpdater;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class MeetingPersonalInfoUpdateService {

    private final MeetingFoodPreferenceUpdater meetingFoodPreferenceUpdater;
    private final MeetingEventDeleteService meetingEventDeleteService;
    private final MeetingEventCreateService meetingEventCreateService;
    private final MeetingService meetingService;
    private final MemberService memberService;

    public MeetingPersonalInfoUpdateService(
        MeetingFoodPreferenceUpdater meetingFoodPreferenceUpdater,
        MeetingEventDeleteService meetingEventDeleteService,
        MeetingEventCreateService meetingEventCreateService,
        MeetingService meetingService, MemberService memberService) {
        this.meetingFoodPreferenceUpdater = meetingFoodPreferenceUpdater;
        this.meetingEventDeleteService = meetingEventDeleteService;
        this.meetingEventCreateService = meetingEventCreateService;
        this.meetingService = meetingService;
        this.memberService = memberService;
    }

    public void updateMeetingPersonalInfo(Long meetingId, Long memberId,
        PersonalPreferenceUpdateRequest personalPreferenceUpdateRequest) {

        Member member = memberService.findMember(memberId);
        Meeting meeting = meetingService.findMeeting(meetingId);
        MemberMeeting memberMeeting = meetingService.findMemberMeeting(member, meeting);

        meetingEventDeleteService.deleteMeetingEvent(memberMeeting);
        meetingFoodPreferenceUpdater.updatePreferences(
            meetingId,
            memberId,
            personalPreferenceUpdateRequest.preferences(),
            personalPreferenceUpdateRequest.nonPreferences()
        );
        meetingEventCreateService.saveMeetingAvoidTime(memberMeeting, personalPreferenceUpdateRequest.times());

    }
}
