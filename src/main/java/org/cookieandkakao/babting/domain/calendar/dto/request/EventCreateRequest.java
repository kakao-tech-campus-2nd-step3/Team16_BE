package org.cookieandkakao.babting.domain.calendar.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventCreateRequest(
    @NotBlank(message = "제목은 비어있으면 안됩니다!")
    String title,

    @NotNull(message = "시간은 필수입니다.")
    TimeCreateRequest time,

    String rrule,

    String description
) {

}
