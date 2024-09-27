package org.cookieandkakao.babting.domain.food.dto;

public class NonPreferenceFoodDto {
    private Long foodId;

    public NonPreferenceFoodDto() {
    }

    public NonPreferenceFoodDto(Long foodId) {
        this.foodId = foodId;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }
}
