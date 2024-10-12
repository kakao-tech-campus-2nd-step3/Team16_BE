package org.cookieandkakao.babting.domain.food.dto;

import org.cookieandkakao.babting.domain.food.entity.Food;

public record FoodGetResponse(
        Long foodId,
        String category,
        String name
) {
    public static FoodGetResponse from(Food food){
        return new FoodGetResponse(food.getFoodId(), food.getFoodCategory().getName(), food.getName());
    }
}
