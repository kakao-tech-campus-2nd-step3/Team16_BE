package org.cookieandkakao.babting.domain.meeting.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;

public record MeetingInfoGetResponse(
    String title,

    LocalDate startDate,

    LocalDate endDate,

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", example = "08:00:00", description = "HH:mm:ss 형식의 시간")
    LocalTime startTime,

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", example = "08:00:00", description = "HH:mm:ss 형식의 시간")
    LocalTime endTime
) {
    public static MeetingInfoGetResponse from(Meeting meeting) {
        return new MeetingInfoGetResponse(meeting.getTitle(), meeting.getStartDate(),
            meeting.getEndDate(),
            meeting.getStartTime(), meeting.getEndTime()
        );
    }
}
