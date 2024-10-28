package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.common.exception.customexception.FoodNotFoundException;
import org.cookieandkakao.babting.common.exception.customexception.PreferenceConflictException;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.repository.FoodRepository;
import org.cookieandkakao.babting.domain.food.repository.NonPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.repository.PreferenceFoodRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class FoodRepositoryService {

    private final FoodRepository foodRepository;
    private final NonPreferenceFoodRepository nonPreferenceFoodRepository;
    private final PreferenceFoodRepository preferenceFoodRepository;

    public FoodRepositoryService(
            FoodRepository foodRepository,
            NonPreferenceFoodRepository nonPreferenceFoodRepository,
            PreferenceFoodRepository preferenceFoodRepository
    ) {
        this.foodRepository = foodRepository;
        this.nonPreferenceFoodRepository = nonPreferenceFoodRepository;
        this.preferenceFoodRepository = preferenceFoodRepository;
    }

    public Food findFoodById(Long foodId) {
        return foodRepository.findById(foodId)
                .orElseThrow(() -> new FoodNotFoundException("해당 음식을 찾을 수 없습니다."));
    }

    public void validateNotAlreadyPreferredOrNonPreferred(Food food, Member member) {
        if (nonPreferenceFoodRepository.existsByFoodAndMember(food, member)) {
            throw new PreferenceConflictException("해당 음식은 이미 비선호 음식으로 등록되어 있습니다.");
        }

        if (preferenceFoodRepository.existsByFoodAndMember(food, member)) {
            throw new PreferenceConflictException("해당 음식은 이미 선호 음식으로 등록되어 있습니다.");
        }
    }

    public List<Food> findFoodsByIds(Set<Long> foodIds) {
        return foodRepository.findAllById(foodIds);
    }
}
