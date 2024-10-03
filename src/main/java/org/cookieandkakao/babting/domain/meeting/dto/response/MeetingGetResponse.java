package org.cookieandkakao.babting.domain.meeting.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record MeetingGetResponse(
    LocationGetResponse baseLocation,

    String title,

    LocalDate startDate,

    LocalDate endDate,

    Integer durationTime,

    LocalTime startTime,

    LocalTime endTime
) {
}
