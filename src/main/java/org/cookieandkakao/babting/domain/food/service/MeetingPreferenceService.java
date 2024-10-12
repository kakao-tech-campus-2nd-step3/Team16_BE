package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.entity.NonPreferenceFood;
import org.cookieandkakao.babting.domain.food.entity.PreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.NonPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.repository.PreferenceFoodRepository;
import org.cookieandkakao.babting.domain.meeting.repository.MemberMeetingRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MeetingPreferenceService {
    private final MemberMeetingRepository memberMeetingRepository;
    private final PreferenceFoodRepository preferenceFoodRepository;
    private final NonPreferenceFoodRepository nonPreferenceFoodRepository;

    public MeetingPreferenceService(MemberMeetingRepository memberMeetingRepository,
                                    PreferenceFoodRepository preferenceFoodRepository,
                                    NonPreferenceFoodRepository nonPreferenceFoodRepository) {
        this.memberMeetingRepository = memberMeetingRepository;
        this.preferenceFoodRepository = preferenceFoodRepository;
        this.nonPreferenceFoodRepository = nonPreferenceFoodRepository;
    }

    public Set<Long> getRecommendedFoodsForMeeting(Long meetingId) {
        // 특정 모임의 모든 멤버 조회
        List<Member> members = memberMeetingRepository.findMembersByMeetingId(meetingId);

        // 모든 멤버의 선호 음식 ID를 Set으로 수집
        Set<Long> preferredFoodIds = new HashSet<>();
        for (Member member : members) {
            List<PreferenceFood> preferenceFoods = preferenceFoodRepository.findAllByMember(member);
            preferredFoodIds.addAll(preferenceFoods.stream()
                    .map(preference -> preference.getFood().getFoodId())
                    .collect(Collectors.toSet()));
        }

        // 모든 멤버의 비선호 음식 ID를 Set으로 수집하고 선호 음식 ID에서 제거
        for (Member member : members) {
            List<NonPreferenceFood> nonPreferenceFoods = nonPreferenceFoodRepository.findAllByMember(member);
            for (NonPreferenceFood nonPreference : nonPreferenceFoods) {
                preferredFoodIds.remove(nonPreference.getFood().getFoodId());
            }
        }

        return preferredFoodIds;
    }
}
