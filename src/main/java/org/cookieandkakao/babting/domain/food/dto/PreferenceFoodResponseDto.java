package org.cookieandkakao.babting.domain.food.dto;

public class PreferenceFoodResponseDto {
    private Long foodId;
    private String category;
    private String name;

    public PreferenceFoodResponseDto(Long foodId, String category, String name) {
        this.foodId = foodId;
        this.category = category;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
