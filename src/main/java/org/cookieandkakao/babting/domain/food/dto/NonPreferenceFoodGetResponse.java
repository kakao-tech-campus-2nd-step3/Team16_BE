package org.cookieandkakao.babting.domain.food.dto;

public record NonPreferenceFoodGetResponse(
        Long foodId,
        String category,
        String name
) {
}
