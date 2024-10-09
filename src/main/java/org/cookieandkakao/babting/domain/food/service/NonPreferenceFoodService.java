package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceCreateRequest;
import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.NonPreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.NonPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NonPreferenceFoodService implements FoodPreferenceStrategy {

    private final NonPreferenceFoodRepository nonPreferenceFoodRepository;
    private final FoodRepositoryService foodRepositoryService;
    private final MemberService memberService;

    public NonPreferenceFoodService(NonPreferenceFoodRepository nonPreferenceFoodRepository,
                                    FoodRepositoryService foodRepositoryService,
                                    MemberService memberService
    ) {
        this.nonPreferenceFoodRepository = nonPreferenceFoodRepository;
        this.foodRepositoryService = foodRepositoryService;
        this.memberService = memberService;
    }

    @Override
    public List<FoodPreferenceGetResponse> getAllPreferencesByMember(Long memberId) {
        return nonPreferenceFoodRepository.findAll().stream()
                .map(nonPreferenceFood -> new FoodPreferenceGetResponse(
                        nonPreferenceFood.getFood().getFoodId(),
                        nonPreferenceFood.getFood().getFoodCategory().getName(),
                        nonPreferenceFood.getFood().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public FoodPreferenceGetResponse addPreference(FoodPreferenceCreateRequest request, Long memberId) {
        Food food = foodRepositoryService.findFoodById(request.foodId());
        Member member = memberService.findMember(memberId);

        NonPreferenceFood nonPreferenceFood = new NonPreferenceFood(food, member);

        foodRepositoryService.validateNotAlreadyPreferredOrNonPreferred(food, member);

        NonPreferenceFood savedNonPreference = nonPreferenceFoodRepository.save(nonPreferenceFood);
        return new FoodPreferenceGetResponse(savedNonPreference.getFood().getFoodId(),
                savedNonPreference.getFood().getFoodCategory().getName(),
                savedNonPreference.getFood().getName());
    }

    @Transactional
    @Override
    public void deletePreference(Long foodId, Long memberId) {
        Food food = foodRepositoryService.findFoodById(foodId);
        Member member = memberService.findMember(memberId);

        nonPreferenceFoodRepository.findByFoodAndMember(food, member)
                .orElseThrow(() -> new RuntimeException("해당 비선호 음식을 찾을 수 없습니다."));
        nonPreferenceFoodRepository.deleteByFoodAndMember(food, member);
    }
}
