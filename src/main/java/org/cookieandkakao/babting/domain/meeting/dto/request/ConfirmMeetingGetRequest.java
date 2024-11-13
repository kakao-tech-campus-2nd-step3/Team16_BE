package org.cookieandkakao.babting.domain.meeting.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

// Todo MeetingConfirmGetRequest로 변경
public record ConfirmMeetingGetRequest(
    @NotNull
    LocalDateTime confirmDateTime,
    Long confirmFoodId
) {
}
