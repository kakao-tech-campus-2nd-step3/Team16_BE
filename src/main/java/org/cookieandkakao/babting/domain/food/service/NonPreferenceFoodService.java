package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.NonPreferenceFoodCreateRequestDto;
import org.cookieandkakao.babting.domain.food.dto.NonPreferenceFoodGetResponseDto;
import org.cookieandkakao.babting.domain.food.entity.NonPreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.NonPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.repository.PreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NonPreferenceFoodService {

    private final NonPreferenceFoodRepository nonPreferenceFoodRepository;
    private final PreferenceFoodRepository preferenceFoodRepository;
    private final FoodRepository foodRepository;

    public NonPreferenceFoodService(NonPreferenceFoodRepository nonPreferenceFoodRepository, PreferenceFoodRepository preferenceFoodRepository, FoodRepository foodRepository){
        this.nonPreferenceFoodRepository = nonPreferenceFoodRepository;
        this.preferenceFoodRepository = preferenceFoodRepository;
        this.foodRepository = foodRepository;
    }

    //비선호 음식 조희
    public List<NonPreferenceFoodGetResponseDto> getAllNonPreferences() {
        List<NonPreferenceFood> nonPreferences = nonPreferenceFoodRepository.findAll();
        return nonPreferences.stream()
                .map(nonPreferenceFood -> new NonPreferenceFoodGetResponseDto(
                        nonPreferenceFood.getFood().getFoodId(),
                        nonPreferenceFood.getFood().getFoodCategory().getName(),
                        nonPreferenceFood.getFood().getName()))
                .collect(Collectors.toList());
    }

    // 비선호 음식 추가
    public NonPreferenceFoodGetResponseDto addNonPreference(NonPreferenceFoodCreateRequestDto nonPreferenceFoodCreateRequestDto) {
        Food food = foodRepository.findById(nonPreferenceFoodCreateRequestDto.getFoodId())
                .orElseThrow(() -> new RuntimeException("해당 음식을 찾을 수 없습니다."));

        // 이미 선호 음식으로 등록되어 있는지 확인
        boolean isAlreadyPreferred = preferenceFoodRepository.existsByFood(food);
        if (isAlreadyPreferred) {
            throw new RuntimeException("해당 음식은 이미 선호 음식으로 등록되어 있습니다.");
        }

        // 이미 비선호 음식으로 등록되어 있는지 확인
        boolean isAlreadyNonPreferred = nonPreferenceFoodRepository.existsByFood(food);
        if (isAlreadyNonPreferred) {
            throw new RuntimeException("해당 음식은 이미 비선호 음식으로 등록되어 있습니다.");
        }

        NonPreferenceFood nonPreferenceFood = new NonPreferenceFood();
        nonPreferenceFood.setFood(food);
        NonPreferenceFood savedNonPreference = nonPreferenceFoodRepository.save(nonPreferenceFood);

        return new NonPreferenceFoodGetResponseDto(
                savedNonPreference.getFood().getFoodId(),
                savedNonPreference.getFood().getFoodCategory().getName(),
                savedNonPreference.getFood().getName());
    }

    // 비선호 음식 삭제
    public void deleteNonPreference(NonPreferenceFoodCreateRequestDto nonPreferenceFoodCreateRequestDto) {
        foodRepository.findById(nonPreferenceFoodCreateRequestDto.getFoodId())
                .orElseThrow(() -> new RuntimeException("해당 음식을 찾을 수 없습니다."));

        boolean exists = nonPreferenceFoodRepository.existsById(nonPreferenceFoodCreateRequestDto.getFoodId());
        if (!exists) {
            throw new RuntimeException("해당 비선호 음식을 찾을 수 없습니다.");
        }
        nonPreferenceFoodRepository.deleteById(nonPreferenceFoodCreateRequestDto.getFoodId());
    }
}
