package org.cookieandkakao.babting.domain.meeting.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import org.cookieandkakao.babting.common.util.TimeFormatterUtil;
import org.cookieandkakao.babting.domain.calendar.entity.Time;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MeetingTimeCreateRequest(
    @NotBlank(message = "시작 시간은 필수입니다.")
    String startAt,

    @NotBlank(message = "종료 시간은 필수입니다.")
    String endAt,

    String timeZone,

    boolean allDay
) {
    public Time toEntity() {
        LocalDateTime start = LocalDateTime.parse(startAt, TimeFormatterUtil.FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endAt, TimeFormatterUtil.FORMATTER);
        return new Time(start, end, this.timeZone, this.allDay);
    }
}
