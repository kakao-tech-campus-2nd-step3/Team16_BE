package org.cookieandkakao.babting.domain.meeting.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;

public record MeetingUpdateRequest(
    @NotNull
    LocationCreateRequest baseLocation,

    @NotNull
    String title,

    @NotNull
    @FutureOrPresent
    LocalDate startDate,

    @NotNull
    @FutureOrPresent
    LocalDate endDate,

    @NotNull
    Integer durationTime,

    @NotNull
    LocalTime startTime,

    @NotNull
    LocalTime endTime
) {
}
