package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import java.util.List;

public interface MeetingFoodPreferenceStrategy {
    List<FoodPreferenceGetResponse> getAllPreferencesByMeeting(Long meetingId, Long memberId);
}
