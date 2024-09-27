package org.cookieandkakao.babting.domain.food.dto;

public class PreferenceFoodCreateRequestDto {
    private Long foodId;

    public PreferenceFoodCreateRequestDto() {
    }

    public PreferenceFoodCreateRequestDto(Long foodId) {
        this.foodId = foodId;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }
}
