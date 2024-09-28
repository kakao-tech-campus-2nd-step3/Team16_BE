package org.cookieandkakao.babting.domain.food.dto;

public record PreferenceFoodGetResponse(
        Long foodId,
        String category,
        String name
) {
}
