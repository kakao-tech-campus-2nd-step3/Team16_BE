package org.cookieandkakao.babting.domain.food.dto;

import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.FoodCategory;

public record FoodGetResponseDto(Long foodId, String category, String name) {

    public Food toEntity(FoodCategory foodCategory) {
        return new Food(this.foodId, foodCategory, this.name);
    }
}
