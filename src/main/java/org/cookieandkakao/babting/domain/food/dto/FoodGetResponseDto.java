package org.cookieandkakao.babting.domain.food.dto;

public class FoodGetResponseDto {
    private Long foodId;
    private String category;
    private String name;

    public FoodGetResponseDto(Long foodId, String category, String name) {
        this.foodId = foodId;
        this.category = category;
        this.name = name;
    }

    public Long getFoodId() {
        return foodId;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }
}

