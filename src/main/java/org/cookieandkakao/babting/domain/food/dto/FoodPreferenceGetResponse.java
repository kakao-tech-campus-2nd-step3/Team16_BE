package org.cookieandkakao.babting.domain.food.dto;

public record FoodPreferenceGetResponse(
        Long foodId,
        String category,
        String name
) {
}
