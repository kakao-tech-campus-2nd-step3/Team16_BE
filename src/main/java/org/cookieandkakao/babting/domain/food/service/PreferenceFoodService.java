package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceCreateRequest;
import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.PreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.PreferenceFoodRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PreferenceFoodService implements FoodPreferenceStrategy {

    private final PreferenceFoodRepository preferenceFoodRepository;
    private final FoodRepositoryService foodRepositoryService;
    private final MemberService memberService;

    public PreferenceFoodService(PreferenceFoodRepository preferenceFoodRepository,
                                 FoodRepositoryService foodRepositoryService,
                                 MemberService memberService
    ) {
        this.preferenceFoodRepository = preferenceFoodRepository;
        this.foodRepositoryService = foodRepositoryService;
        this.memberService = memberService;
    }

    @Override
    public List<FoodPreferenceGetResponse> getAllPreferencesByMember(Long memberId) {
        Member member = memberService.findMember(memberId);

        return preferenceFoodRepository.findAllByMember(member).stream()
                .map(preferenceFood -> new FoodPreferenceGetResponse(
                        preferenceFood.getFood().getFoodId(),
                        preferenceFood.getFood().getFoodCategory().getName(),
                        preferenceFood.getFood().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public FoodPreferenceGetResponse addPreference(FoodPreferenceCreateRequest request, Long memberId) {
        Food food = foodRepositoryService.findFoodById(request.foodId());
        Member member = memberService.findMember(memberId);

        PreferenceFood preferenceFood = new PreferenceFood(food, member);

        foodRepositoryService.validateNotAlreadyPreferredOrNonPreferred(food, member);

        PreferenceFood savedPreference = preferenceFoodRepository.save(preferenceFood);
        return new FoodPreferenceGetResponse(savedPreference.getFood().getFoodId(),
                savedPreference.getFood().getFoodCategory().getName(),
                savedPreference.getFood().getName());
    }

    @Transactional
    @Override
    public void deletePreference(Long foodId, Long memberId) {
        Food food = foodRepositoryService.findFoodById(foodId);
        Member member = memberService.findMember(memberId);

        preferenceFoodRepository.findByFoodAndMember(food, member)
                .orElseThrow(() -> new RuntimeException("해당 선호 음식을 찾을 수 없습니다."));
        preferenceFoodRepository.deleteByFoodAndMember(food, member);
    }
}
