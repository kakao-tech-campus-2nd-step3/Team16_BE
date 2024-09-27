package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.PreferenceFoodCreateRequestDto;
import org.cookieandkakao.babting.domain.food.dto.PreferenceFoodGetResponseDto;
import org.cookieandkakao.babting.domain.food.entity.PreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.NonPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.repository.PreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PreferenceFoodService {

    private final PreferenceFoodRepository preferenceFoodRepository;
    private final NonPreferenceFoodRepository nonPreferenceFoodRepository;
    private final FoodRepository foodRepository;

    public PreferenceFoodService(NonPreferenceFoodRepository nonPreferenceFoodRepository, PreferenceFoodRepository preferenceFoodRepository, FoodRepository foodRepository){
        this.nonPreferenceFoodRepository = nonPreferenceFoodRepository;
        this.preferenceFoodRepository = preferenceFoodRepository;
        this.foodRepository = foodRepository;
    }

    //선호 음식 조회
    public List<PreferenceFoodGetResponseDto> getAllPreferences() {
        List<PreferenceFood> preferences = preferenceFoodRepository.findAll();
        return preferences.stream()
                .map(preferenceFood -> new PreferenceFoodGetResponseDto(
                        preferenceFood.getFood().getFoodId(),
                        preferenceFood.getFood().getFoodCategory().getName(),
                        preferenceFood.getFood().getName()))
                .collect(Collectors.toList());
    }

    // 선호 음식 추가
    public PreferenceFoodGetResponseDto addPreference(PreferenceFoodCreateRequestDto preferenceFoodCreateRequestDto) {
        Food food = foodRepository.findById(preferenceFoodCreateRequestDto.foodId())
                .orElseThrow(() -> new RuntimeException("해당 음식을 찾을 수 없습니다."));

        // 이미 비선호 음식으로 등록되어 있는지 확인
        boolean isAlreadyNonPreferred = nonPreferenceFoodRepository.existsByFood(food);
        if (isAlreadyNonPreferred) {
            throw new RuntimeException("해당 음식은 이미 비선호 음식으로 등록되어 있습니다.");
        }

        // 이미 선호 음식으로 등록되어 있는지 확인
        boolean isAlreadyPreferred = preferenceFoodRepository.existsByFood(food);
        if (isAlreadyPreferred) {
            throw new RuntimeException("해당 음식은 이미 선호 음식으로 등록되어 있습니다.");
        }

        PreferenceFood preferenceFood = new PreferenceFood();
        preferenceFood.setFood(food);
        PreferenceFood savedPreference = preferenceFoodRepository.save(preferenceFood);

        return new PreferenceFoodGetResponseDto(
                savedPreference.getFood().getFoodId(),
                savedPreference.getFood().getFoodCategory().getName(),
                savedPreference.getFood().getName());
    }

    // 선호 음식 삭제
    public void deletePreference(PreferenceFoodCreateRequestDto preferenceFoodCreateRequestDto) {
        foodRepository.findById(preferenceFoodCreateRequestDto.foodId())
                .orElseThrow(() -> new RuntimeException("해당 음식을 찾을 수 없습니다."));

        boolean exists = preferenceFoodRepository.existsById(preferenceFoodCreateRequestDto.foodId());
        if (!exists) {
            throw new RuntimeException("해당 선호 음식을 찾을 수 없습니다.");
        }

        preferenceFoodRepository.deleteById(preferenceFoodCreateRequestDto.foodId());
    }
}
