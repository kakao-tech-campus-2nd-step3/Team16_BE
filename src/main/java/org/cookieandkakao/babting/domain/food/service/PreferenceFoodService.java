package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.PreferenceFoodDto;
import org.cookieandkakao.babting.domain.food.entity.PreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.PreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreferenceFoodService {

    @Autowired
    private PreferenceFoodRepository preferenceFoodRepository;

    @Autowired
    private FoodRepository foodRepository;

    // 선호 음식 전체 조회
    public List<PreferenceFood> getAllPreferences() {
        return preferenceFoodRepository.findAll();
    }

    // 선호 음식 추가
    public PreferenceFood addPreference(PreferenceFoodDto preferenceFoodDto) {
        Food food = foodRepository.findById(preferenceFoodDto.getFoodId())
                .orElseThrow(() -> new RuntimeException("해당 음식을 찾을 수 없습니다."));

        PreferenceFood preferenceFood = new PreferenceFood();
        preferenceFood.setFood(food);
        return preferenceFoodRepository.save(preferenceFood);
    }

    // 선호 음식 삭제
    public void deletePreference(Long id) {
        preferenceFoodRepository.deleteById(id);
    }
}
