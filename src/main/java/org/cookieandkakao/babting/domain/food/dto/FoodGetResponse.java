package org.cookieandkakao.babting.domain.food.dto;

public record FoodGetResponse(
        Long foodId,
        String category,
        String name
) {
}
