package org.cookieandkakao.babting.domain.meeting.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;

public record MeetingCreateRequest(
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
    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime startTime,

    @NotNull
    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime endTime
) {
    public Meeting toEntity(){
        return new Meeting(baseLocation.toEntity(), title, startDate, endDate, durationTime,
            startTime, endTime);
    }
}
