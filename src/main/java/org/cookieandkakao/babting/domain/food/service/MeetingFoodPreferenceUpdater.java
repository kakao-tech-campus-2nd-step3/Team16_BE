package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.MeetingNonPreferenceFood;
import org.cookieandkakao.babting.domain.food.entity.MeetingPreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.MeetingNonPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.repository.MeetingPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.service.MeetingService;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MeetingFoodPreferenceUpdater {
    private final MeetingPreferenceFoodRepository meetingPreferenceFoodRepository;
    private final MeetingNonPreferenceFoodRepository meetingNonPreferenceFoodRepository;
    private final FoodRepositoryService foodRepositoryService;
    private final MemberService memberService;
    private final MeetingService meetingService;

    public MeetingFoodPreferenceUpdater(
            MeetingPreferenceFoodRepository meetingPreferenceFoodRepository,
            MeetingNonPreferenceFoodRepository meetingNonPreferenceFoodRepository,
            FoodRepositoryService foodRepositoryService,
            MemberService memberService,
            MeetingService meetingService
    ) {
        this.meetingPreferenceFoodRepository = meetingPreferenceFoodRepository;
        this.meetingNonPreferenceFoodRepository = meetingNonPreferenceFoodRepository;
        this.foodRepositoryService = foodRepositoryService;
        this.memberService = memberService;
        this.meetingService = meetingService;
    }

    @Transactional
    public void updatePreferences(Long meetingId, Long memberId, List<Long> preferences, List<Long> nonPreferences) {
        Member member = memberService.findMember(memberId);
        Meeting meeting = meetingService.findMeeting(meetingId);
        MemberMeeting memberMeeting = meetingService.findMemberMeeting(member, meeting);

        meetingPreferenceFoodRepository.deleteAllByMemberMeeting(memberMeeting);
        meetingNonPreferenceFoodRepository.deleteAllByMemberMeeting(memberMeeting);

        // 새로운 선호 음식 추가
        preferences.forEach(foodId -> {
            Food food = foodRepositoryService.findFoodById(foodId);
            MeetingPreferenceFood preferenceFood = new MeetingPreferenceFood(food, memberMeeting);
            meetingPreferenceFoodRepository.save(preferenceFood);
        });

        // 새로운 비선호 음식 추가
        nonPreferences.forEach(foodId -> {
            Food food = foodRepositoryService.findFoodById(foodId);
            MeetingNonPreferenceFood nonPreferenceFood = new MeetingNonPreferenceFood(food, memberMeeting);
            meetingNonPreferenceFoodRepository.save(nonPreferenceFood);
        });
    }
}
