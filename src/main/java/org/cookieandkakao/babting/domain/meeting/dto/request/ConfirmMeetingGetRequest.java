package org.cookieandkakao.babting.domain.meeting.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ConfirmMeetingGetRequest(
    @NotNull
    LocalDateTime confirmDateTime,
    Long confirmFoodId
) {
}
