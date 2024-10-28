package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.MeetingNonPreferenceFood;
import org.cookieandkakao.babting.domain.food.entity.MeetingPreferenceFood;
import org.cookieandkakao.babting.domain.food.entity.NonPreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.MeetingNonPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.repository.MeetingPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.repository.NonPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.repository.MemberMeetingRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MeetingPreferenceService {
    private final MemberMeetingRepository memberMeetingRepository;
    private final MeetingPreferenceFoodRepository meetingPreferenceFoodRepository;
    private final MeetingNonPreferenceFoodRepository meetingNonPreferenceFoodRepository;
    private final NonPreferenceFoodRepository nonPreferenceFoodRepository;
    private final FoodRepositoryService foodRepositoryService;

    public MeetingPreferenceService(MemberMeetingRepository memberMeetingRepository,
                                    MeetingPreferenceFoodRepository meetingPreferenceFoodRepository,
                                    MeetingNonPreferenceFoodRepository meetingNonPreferenceFoodRepository,
                                    NonPreferenceFoodRepository nonPreferenceFoodRepository,
                                    FoodRepositoryService foodRepositoryService
    ) {
        this.memberMeetingRepository = memberMeetingRepository;
        this.meetingPreferenceFoodRepository = meetingPreferenceFoodRepository;
        this.meetingNonPreferenceFoodRepository = meetingNonPreferenceFoodRepository;
        this.nonPreferenceFoodRepository = nonPreferenceFoodRepository;
        this.foodRepositoryService = foodRepositoryService;
    }

    public List<FoodPreferenceGetResponse> getRecommendedFoodDetailsForMeeting(Long meetingId) {
        List<Food> recommendedFoods = getRecommendedFoodsForMeeting(meetingId);
        return recommendedFoods.stream()
                .map(FoodPreferenceGetResponse::fromFood)
                .collect(Collectors.toList());
    }

    private List<Food> getRecommendedFoodsForMeeting(Long meetingId) {
        List<MemberMeeting> memberMeetings = memberMeetingRepository.findMemberMeetingsByMeetingId(meetingId);

        // 모든 멤버의 모임별 선호 음식 ID를 Set으로 수집
        Set<Long> preferredFoodIds = new HashSet<>();
        for (MemberMeeting memberMeeting : memberMeetings) {
            List<MeetingPreferenceFood> preferenceFoods = meetingPreferenceFoodRepository.findAllByMemberMeeting(memberMeeting);
            preferredFoodIds.addAll(preferenceFoods.stream()
                    .map(preference -> preference.getFood().getFoodId())
                    .collect(Collectors.toSet()));
        }

        // 모든 멤버의 모임별 비선호 음식 ID를 Set으로 수집하고 선호 음식 ID에서 제거
        for (MemberMeeting memberMeeting : memberMeetings) {
            List<MeetingNonPreferenceFood> nonPreferenceFoods = meetingNonPreferenceFoodRepository.findAllByMemberMeeting(memberMeeting);
            for (MeetingNonPreferenceFood nonPreference : nonPreferenceFoods) {
                preferredFoodIds.remove(nonPreference.getFood().getFoodId());
            }
        }

        // 모든 멤버의 개인 비선호 음식 ID를 Set으로 수집하고 선호 음식 ID에서 제거
        for (MemberMeeting memberMeeting : memberMeetings) {
            List<NonPreferenceFood> nonPreferenceFoods = nonPreferenceFoodRepository.findAllByMember(memberMeeting.getMember());
            for (NonPreferenceFood nonPreference : nonPreferenceFoods) {
                preferredFoodIds.remove(nonPreference.getFood().getFoodId());
            }
        }

        return foodRepositoryService.findFoodsByIds(preferredFoodIds);
    }
}
