package org.cookieandkakao.babting.domain.food.dto;

public record FoodGetResponseDto(
        Long foodId,
        String category,
        String name
) {
}
