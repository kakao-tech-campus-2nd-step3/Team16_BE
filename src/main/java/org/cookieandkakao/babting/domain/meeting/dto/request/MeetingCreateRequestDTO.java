package org.cookieandkakao.babting.domain.meeting.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;

public record MeetingCreateRequestDTO (
    @NotNull
    LocationCreateRequestDTO baseLocation,

    @NotNull
    String title,

    @NotNull
    LocalDate startDate,

    @NotNull
    LocalDate endDate,

    @NotNull
    Integer durationTime,

    @NotNull
    LocalTime startTime,

    @NotNull
    LocalTime endTime,

    LocalDateTime confirmDateTime

) {
    public Meeting toEntity(){
        return new Meeting(baseLocation.toEntity(), title, startDate, endDate, durationTime,
            startTime, endTime, confirmDateTime);
    }
}
