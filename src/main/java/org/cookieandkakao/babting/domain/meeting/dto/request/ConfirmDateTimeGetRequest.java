package org.cookieandkakao.babting.domain.meeting.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ConfirmDateTimeGetRequest (
    @NotNull
    LocalDateTime confirmDateTime
) {
}
