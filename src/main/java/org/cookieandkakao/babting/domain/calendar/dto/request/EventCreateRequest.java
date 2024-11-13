package org.cookieandkakao.babting.domain.calendar.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingEventCreateRequest;

public record EventCreateRequest(
    @NotBlank(message = "제목은 비어있으면 안됩니다!")
    String title,

    @NotNull(message = "시간은 필수입니다.")
    TimeCreateRequest time,

    String rrule,

    List<Integer> reminders,

    String description
) {

    public static EventCreateRequest from(MeetingEventCreateRequest meetingEventCreateRequest) {
        return new EventCreateRequest(
            meetingEventCreateRequest.title(),
            meetingEventCreateRequest.time().toTimeCreateRequest(), null,
            meetingEventCreateRequest.reminders(), null);
    }

}
