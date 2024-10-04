package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.NonPreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.FoodRepository;
import org.cookieandkakao.babting.domain.food.repository.NonPreferenceFoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NonPreferenceFoodService implements FoodPreferenceStrategy {

    private final NonPreferenceFoodRepository nonPreferenceFoodRepository;
    private final FoodRepositoryService foodRepositoryService;

    public NonPreferenceFoodService(NonPreferenceFoodRepository nonPreferenceFoodRepository, FoodRepository foodRepository, FoodRepositoryService foodRepositoryService) {
        this.nonPreferenceFoodRepository = nonPreferenceFoodRepository;
        this.foodRepositoryService = foodRepositoryService;
    }

    @Override
    public List<FoodPreferenceGetResponse> getAllPreferences() {
        return nonPreferenceFoodRepository.findAll().stream()
                .map(nonPreferenceFood -> new FoodPreferenceGetResponse(
                        nonPreferenceFood.getFood().getFoodId(),
                        nonPreferenceFood.getFood().getFoodCategory().getName(),
                        nonPreferenceFood.getFood().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public FoodPreferenceGetResponse addPreference(Food food) {
        NonPreferenceFood nonPreferenceFood = new NonPreferenceFood(food);

        foodRepositoryService.validateNotAlreadyPreferredOrNonPreferred(food);

        NonPreferenceFood savedNonPreference = nonPreferenceFoodRepository.save(nonPreferenceFood);
        return new FoodPreferenceGetResponse(savedNonPreference.getFood().getFoodId(),
                savedNonPreference.getFood().getFoodCategory().getName(),
                savedNonPreference.getFood().getName());
    }

    @Override
    public void deletePreference(Long foodId) {
        nonPreferenceFoodRepository.findByFoodId(foodId)
                .orElseThrow(() -> new RuntimeException("해당 비선호 음식을 찾을 수 없습니다."));
        nonPreferenceFoodRepository.deleteById(foodId);
    }
}
