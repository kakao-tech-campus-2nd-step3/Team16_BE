package org.cookieandkakao.babting.domain.meeting.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", example = "08:00:00", description = "HH:mm:ss 형식의 시간")
    LocalTime startTime,

    @NotNull
    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", example = "08:00:00", description = "HH:mm:ss 형식의 시간")
    LocalTime endTime
) {
}
