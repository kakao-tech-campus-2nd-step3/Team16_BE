package org.cookieandkakao.babting.domain.food.dto;

import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.MeetingNonPreferenceFood;
import org.cookieandkakao.babting.domain.food.entity.MeetingPreferenceFood;
import org.cookieandkakao.babting.domain.food.entity.NonPreferenceFood;
import org.cookieandkakao.babting.domain.food.entity.PreferenceFood;

public record FoodPreferenceGetResponse(
    Long foodId,
    String category,
    String name
) {
  public static FoodPreferenceGetResponse fromMeetingPreferenceFood(MeetingPreferenceFood meetingPreferenceFood) {
    return new FoodPreferenceGetResponse(
        meetingPreferenceFood.getFood().getFoodId(),
        meetingPreferenceFood.getFood().getFoodCategory().getName(),
        meetingPreferenceFood.getFood().getName());
  }

  public static FoodPreferenceGetResponse fromMeetingNonPreferenceFood(MeetingNonPreferenceFood meetingNonPreferenceFood) {
    return new FoodPreferenceGetResponse(
        meetingNonPreferenceFood.getFood().getFoodId(),
        meetingNonPreferenceFood.getFood().getFoodCategory().getName(),
        meetingNonPreferenceFood.getFood().getName());
  }

  public static FoodPreferenceGetResponse fromPreferenceFood(PreferenceFood preferenceFood) {
    return new FoodPreferenceGetResponse(
        preferenceFood.getFood().getFoodId(),
        preferenceFood.getFood().getFoodCategory().getName(),
        preferenceFood.getFood().getName());
  }

  public static FoodPreferenceGetResponse fromNonPreferenceFood(NonPreferenceFood nonPreferenceFood) {
    return new FoodPreferenceGetResponse(
        nonPreferenceFood.getFood().getFoodId(),
        nonPreferenceFood.getFood().getFoodCategory().getName(),
        nonPreferenceFood.getFood().getName());
  }

  public static FoodPreferenceGetResponse fromFood(Food food) {
    return new FoodPreferenceGetResponse(
            food.getFoodId(),
            food.getFoodCategory().getName(),
            food.getName());
  }
}
