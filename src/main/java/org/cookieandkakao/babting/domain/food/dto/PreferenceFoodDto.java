package org.cookieandkakao.babting.domain.food.dto;

public class PreferenceFoodDto {
    private Long foodId;

    public PreferenceFoodDto() {
    }

    public PreferenceFoodDto(Long foodId) {
        this.foodId = foodId;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }
}
