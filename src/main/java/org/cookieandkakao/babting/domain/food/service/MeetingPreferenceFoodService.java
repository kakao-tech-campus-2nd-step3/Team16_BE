package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.repository.MeetingPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.service.MeetingService;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetingPreferenceFoodService implements MeetingFoodPreferenceStrategy {
    private final MeetingPreferenceFoodRepository meetingPreferenceFoodRepository;
    private final MeetingService meetingService;
    private final MemberService memberService;

    public MeetingPreferenceFoodService(
            MeetingPreferenceFoodRepository meetingPreferenceFoodRepository,
            MeetingService meetingService,
            MemberService memberService
    ) {
        this.meetingPreferenceFoodRepository = meetingPreferenceFoodRepository;
        this.meetingService = meetingService;
        this.memberService = memberService;
    }

    @Override
    public List<FoodPreferenceGetResponse> getAllPreferencesByMeeting(Long meetingId, Long memberId) {
        Member member = memberService.findMember(memberId);
        Meeting meeting = meetingService.findMeeting(meetingId);

        MemberMeeting memberMeeting = meetingService.findMemberMeeting(member, meeting);

        return meetingPreferenceFoodRepository.findAllByMemberMeeting(memberMeeting).stream()
                .map(meetingPreferenceFood -> new FoodPreferenceGetResponse(
                        meetingPreferenceFood.getFood().getFoodId(),
                        meetingPreferenceFood.getFood().getFoodCategory().getName(),
                        meetingPreferenceFood.getFood().getName()))
                .collect(Collectors.toList());
    }
}
