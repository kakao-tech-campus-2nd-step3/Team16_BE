package org.cookieandkakao.babting.domain.food.dto;

import java.util.List;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingTimeCreateRequest;

public record PersonalPreferenceUpdateRequest(
        List<Long> preferences,
        List<Long> nonPreferences,
        List<MeetingTimeCreateRequest> times
) {
}
