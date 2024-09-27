package org.cookieandkakao.babting.domain.food.dto;

public class PreferenceFoodGetResponseDto {
    private Long foodId;
    private String category;
    private String name;

    public PreferenceFoodGetResponseDto(Long foodId, String category, String name) {
        this.foodId = foodId;
        this.category = category;
        this.name = name;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
