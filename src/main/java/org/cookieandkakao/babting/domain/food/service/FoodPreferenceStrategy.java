package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceCreateRequest;
import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;

import java.util.List;

public interface FoodPreferenceStrategy {
    FoodPreferenceGetResponse addPreference(FoodPreferenceCreateRequest request);

    void deletePreference(Long foodId);

    List<FoodPreferenceGetResponse> getAllPreferences();
}
