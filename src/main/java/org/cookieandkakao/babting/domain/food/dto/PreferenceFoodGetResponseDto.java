package org.cookieandkakao.babting.domain.food.dto;

import org.cookieandkakao.babting.domain.food.entity.PreferenceFood;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.member.entity.Member;

public record PreferenceFoodGetResponseDto(Long foodId, String category, String name) {

    public PreferenceFood toEntity(Food food, Member member) {
        PreferenceFood preferenceFood = new PreferenceFood();
        preferenceFood.setFood(food);
        preferenceFood.setMember(member);
        return preferenceFood;
    }
}
