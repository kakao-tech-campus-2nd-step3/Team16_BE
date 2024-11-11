package org.cookieandkakao.babting.domain.meeting.dto.request;

import java.util.List;

public record MeetingJoinCreateRequest(
    List<Long> preferences,
    List<Long> nonPreferences,
    List<MeetingTimeCreateRequest> times
) {

}
