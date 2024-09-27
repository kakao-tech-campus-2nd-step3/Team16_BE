package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.PreferenceFoodDto;
import org.cookieandkakao.babting.domain.food.dto.PreferenceFoodResponseDto;
import org.cookieandkakao.babting.domain.food.entity.PreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.PreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PreferenceFoodService {

    @Autowired
    private PreferenceFoodRepository preferenceFoodRepository;

    @Autowired
    private FoodRepository foodRepository;

    //선호 음식 조회
    public List<PreferenceFoodResponseDto> getAllPreferences() {
        List<PreferenceFood> preferences = preferenceFoodRepository.findAll();
        return preferences.stream()
                .map(preferenceFood -> new PreferenceFoodResponseDto(
                        preferenceFood.getFood().getFoodId(),
                        preferenceFood.getFood().getFoodCategory().getName(),
                        preferenceFood.getFood().getName()))
                .collect(Collectors.toList());
    }

    // 선호 음식 추가
    public PreferenceFoodResponseDto addPreference(PreferenceFoodDto preferenceFoodDto) {
        Food food = foodRepository.findById(preferenceFoodDto.getFoodId())
                .orElseThrow(() -> new RuntimeException("해당 음식을 찾을 수 없습니다."));

        PreferenceFood preferenceFood = new PreferenceFood();
        preferenceFood.setFood(food);
        PreferenceFood savedPreference = preferenceFoodRepository.save(preferenceFood);

        return new PreferenceFoodResponseDto(
                savedPreference.getFood().getFoodId(),
                savedPreference.getFood().getFoodCategory().getName(),
                savedPreference.getFood().getName());
    }

    // 선호 음식 삭제
    public void deletePreference(PreferenceFoodDto preferenceFoodDto) {
        preferenceFoodRepository.deleteById(preferenceFoodDto.getFoodId());
    }
}
