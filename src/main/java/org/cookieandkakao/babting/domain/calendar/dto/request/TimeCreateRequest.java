package org.cookieandkakao.babting.domain.calendar.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import org.cookieandkakao.babting.common.util.TimeFormatterUtil;
import org.cookieandkakao.babting.domain.calendar.entity.Time;
import org.cookieandkakao.babting.domain.calendar.exception.InvalidTimeRangeException;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TimeCreateRequest(

    @NotBlank(message = "시작 시간은 필수입니다.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z", message = "시작 시간 형식은 yyyy-MM-dd'T'HH:mm:ss'Z'이어야 합니다.")
    String startAt,

    @NotBlank(message = "종료 시간은 필수입니다.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z", message = "종료 시간 형식은 yyyy-MM-dd'T'HH:mm:ss'Z'이어야 합니다.")
    String endAt,

    String timeZone,

    boolean allDay
) {

    @AssertTrue(message = "시작 시간이 종료 시간보다 늦을 수 없습니다.")
    private boolean isValidTimeRange() {
        LocalDateTime start = LocalDateTime.parse(startAt, TimeFormatterUtil.FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endAt, TimeFormatterUtil.FORMATTER);
        return !start.isAfter(end);
    }


    public Time toEntity() {
        if (isValidTimeRange()) {
            throw new InvalidTimeRangeException("시작 시간이 종료 시간보다 늦을 수 없습니다.");
        }
        LocalDateTime start = LocalDateTime.parse(startAt, TimeFormatterUtil.FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endAt, TimeFormatterUtil.FORMATTER);
        return new Time(start, end, this.timeZone, this.allDay);
    }

    private LocalDateTime convertToLocalDateTime(String time) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(time, TimeFormatterUtil.FORMATTER);
        return zonedDateTime.toLocalDateTime();
    }

}
