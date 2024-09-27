package org.cookieandkakao.babting.domain.food.dto;

import org.cookieandkakao.babting.domain.food.entity.NonPreferenceFood;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.member.entity.Member;

public record NonPreferenceFoodCreateRequestDto(Long foodId) {

    public NonPreferenceFood toEntity(Food food, Member member) {
        NonPreferenceFood nonPreferenceFood = new NonPreferenceFood();
        nonPreferenceFood.setFood(food);
        nonPreferenceFood.setMember(member);
        return nonPreferenceFood;
    }
}
