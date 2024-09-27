package org.cookieandkakao.babting.domain.food.dto;

public class NonPreferenceFoodCreateRequestDto {
    private Long foodId;

    public NonPreferenceFoodCreateRequestDto() {
    }

    public NonPreferenceFoodCreateRequestDto(Long foodId) {
        this.foodId = foodId;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }
}
