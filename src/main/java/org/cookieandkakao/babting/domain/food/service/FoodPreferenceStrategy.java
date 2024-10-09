package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceCreateRequest;
import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;

import java.util.List;

public interface FoodPreferenceStrategy {
    FoodPreferenceGetResponse addPreference(FoodPreferenceCreateRequest request, Long memberId);

    void deletePreference(Long foodId, Long memberId);

    List<FoodPreferenceGetResponse> getAllPreferencesByMember(Long memberId);
}
