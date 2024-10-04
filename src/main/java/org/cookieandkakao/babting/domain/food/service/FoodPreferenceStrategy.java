package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.FoodCreateRequest;
import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.entity.Food;

import java.util.List;

public interface FoodPreferenceStrategy {
    FoodPreferenceGetResponse addPreference(FoodCreateRequest request);

    void deletePreference(Long foodId);
    List<FoodPreferenceGetResponse> getAllPreferences();
}
