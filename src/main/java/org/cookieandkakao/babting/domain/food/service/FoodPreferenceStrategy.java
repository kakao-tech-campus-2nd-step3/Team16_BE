package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.entity.Food;

import java.util.List;

public interface FoodPreferenceStrategy {
    FoodPreferenceGetResponse addPreference(Food food);
    void deletePreference(Long foodId);
    List<FoodPreferenceGetResponse> getAllPreferences();
}
