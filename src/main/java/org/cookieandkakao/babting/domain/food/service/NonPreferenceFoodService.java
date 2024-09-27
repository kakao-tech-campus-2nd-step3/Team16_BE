package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.NonPreferenceFoodDto;
import org.cookieandkakao.babting.domain.food.dto.NonPreferenceFoodResponseDto;
import org.cookieandkakao.babting.domain.food.entity.NonPreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.NonPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NonPreferenceFoodService {

    @Autowired
    private NonPreferenceFoodRepository nonPreferenceFoodRepository;

    @Autowired
    private FoodRepository foodRepository;

    //비선호 음식 조희
    public List<NonPreferenceFoodResponseDto> getAllNonPreferences() {
        List<NonPreferenceFood> nonPreferences = nonPreferenceFoodRepository.findAll();
        return nonPreferences.stream()
                .map(nonPreferenceFood -> new NonPreferenceFoodResponseDto(
                        nonPreferenceFood.getFood().getFoodId(),
                        nonPreferenceFood.getFood().getFoodCategory().getName(),
                        nonPreferenceFood.getFood().getName()))
                .collect(Collectors.toList());
    }

    // 비선호 음식 추가
    public NonPreferenceFoodResponseDto addNonPreference(NonPreferenceFoodDto nonPreferenceFoodDto) {
        Food food = foodRepository.findById(nonPreferenceFoodDto.getFoodId())
                .orElseThrow(() -> new RuntimeException("해당 음식을 찾을 수 없습니다."));

        NonPreferenceFood nonPreferenceFood = new NonPreferenceFood();
        nonPreferenceFood.setFood(food);
        NonPreferenceFood savedNonPreference = nonPreferenceFoodRepository.save(nonPreferenceFood);

        return new NonPreferenceFoodResponseDto(
                savedNonPreference.getFood().getFoodId(),
                savedNonPreference.getFood().getFoodCategory().getName(),
                savedNonPreference.getFood().getName());
    }

    // 비선호 음식 삭제
    public void deleteNonPreference(NonPreferenceFoodDto nonPreferenceFoodDto) {
        nonPreferenceFoodRepository.deleteById(nonPreferenceFoodDto.getFoodId());
    }
}
