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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        Set<Long> allFoodIds = new HashSet<>(preferences);
        allFoodIds.addAll(nonPreferences);
        Map<Long, Food> foodsMap = foodRepositoryService.findFoodsByIds(allFoodIds)
                .stream().collect(Collectors.toMap(Food::getFoodId, Function.identity()));

        // 선호음식 stream 생성
        List<MeetingPreferenceFood> newPreferenceFoods = preferences.stream()
                .map(foodId -> new MeetingPreferenceFood(foodsMap.get(foodId), memberMeeting))
                .collect(Collectors.toList());

        // 비선호음식 stream 생성
        List<MeetingNonPreferenceFood> newNonPreferenceFoods = nonPreferences.stream()
                .map(foodId -> new MeetingNonPreferenceFood(foodsMap.get(foodId), memberMeeting))
                .collect(Collectors.toList());

        meetingPreferenceFoodRepository.saveAll(newPreferenceFoods);
        meetingNonPreferenceFoodRepository.saveAll(newNonPreferenceFoods);
    }
}
