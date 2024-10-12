package org.cookieandkakao.babting.domain.meeting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MeetingEventCreateRequest(
    @NotBlank(message = "제목은 비어있으면 안됩니다!")
    String title,

    @NotNull(message = "시간은 필수입니다.")
    MeetingTimeCreateRequest time

) {

}
