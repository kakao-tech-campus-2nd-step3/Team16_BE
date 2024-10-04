package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceCreateRequest;
import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.PreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.PreferenceFoodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PreferenceFoodService implements FoodPreferenceStrategy {

    private final PreferenceFoodRepository preferenceFoodRepository;
    private final FoodRepositoryService foodRepositoryService;

    public PreferenceFoodService(PreferenceFoodRepository preferenceFoodRepository,
                                 FoodRepositoryService foodRepositoryService
    ) {
        this.preferenceFoodRepository = preferenceFoodRepository;
        this.foodRepositoryService = foodRepositoryService;
    }

    @Override
    public List<FoodPreferenceGetResponse> getAllPreferences() {
        return preferenceFoodRepository.findAll().stream()
                .map(preferenceFood -> new FoodPreferenceGetResponse(
                        preferenceFood.getFood().getFoodId(),
                        preferenceFood.getFood().getFoodCategory().getName(),
                        preferenceFood.getFood().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public FoodPreferenceGetResponse addPreference(FoodPreferenceCreateRequest request) {
        Food food = foodRepositoryService.findFoodById(request.foodId());
        PreferenceFood preferenceFood = new PreferenceFood(food);

        foodRepositoryService.validateNotAlreadyPreferredOrNonPreferred(food);

        PreferenceFood savedPreference = preferenceFoodRepository.save(preferenceFood);
        return new FoodPreferenceGetResponse(savedPreference.getFood().getFoodId(),
                savedPreference.getFood().getFoodCategory().getName(),
                savedPreference.getFood().getName());
    }

    @Transactional
    @Override
    public void deletePreference(Long foodId) {
        Food food = foodRepositoryService.findFoodById(foodId);

        preferenceFoodRepository.findByFood(food)
                .orElseThrow(() -> new RuntimeException("해당 선호 음식을 찾을 수 없습니다."));
        preferenceFoodRepository.deleteByFood(food);
    }
}
