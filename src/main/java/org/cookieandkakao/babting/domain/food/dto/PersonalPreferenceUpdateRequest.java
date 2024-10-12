package org.cookieandkakao.babting.domain.food.dto;

import java.util.List;

public record PersonalPreferenceUpdateRequest(
        List<Long> preferences,
        List<Long> nonPreferences
) {
}
