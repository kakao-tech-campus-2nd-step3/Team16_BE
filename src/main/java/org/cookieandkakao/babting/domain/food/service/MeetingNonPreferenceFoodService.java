package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.repository.MeetingNonPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.service.MeetingService;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetingNonPreferenceFoodService implements MeetingFoodPreferenceStrategy {
    private final MeetingNonPreferenceFoodRepository meetingNonPreferenceFoodRepository;
    private final MeetingService meetingService;
    private final MemberService memberService;

    public MeetingNonPreferenceFoodService(
            MeetingNonPreferenceFoodRepository meetingNonPreferenceFoodRepository,
            MeetingService meetingService,
            MemberService memberService
    ) {
        this.meetingNonPreferenceFoodRepository = meetingNonPreferenceFoodRepository;
        this.meetingService = meetingService;
        this.memberService = memberService;
    }

    @Override
    public List<FoodPreferenceGetResponse> getAllPreferencesByMeeting(Long meetingId, Long memberId) {
        Member member = memberService.findMember(memberId);
        Meeting meeting = meetingService.findMeeting(meetingId);

        MemberMeeting memberMeeting = meetingService.findMemberMeeting(member, meeting);

        return meetingNonPreferenceFoodRepository.findAllByMemberMeeting(memberMeeting).stream()
                .map(meetingNonPreferenceFood -> new FoodPreferenceGetResponse(
                        meetingNonPreferenceFood.getFood().getFoodId(),
                        meetingNonPreferenceFood.getFood().getFoodCategory().getName(),
                        meetingNonPreferenceFood.getFood().getName()))
                .collect(Collectors.toList());
    }
}

