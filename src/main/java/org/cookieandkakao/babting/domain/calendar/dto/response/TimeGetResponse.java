package org.cookieandkakao.babting.domain.calendar.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import org.cookieandkakao.babting.common.util.TimeFormatterUtil;
import org.cookieandkakao.babting.domain.calendar.entity.Time;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TimeGetResponse(

    String startAt,

    String endAt,

    String timeZone,

    boolean allDay
) {


    public Time toEntity() {
        LocalDateTime start = LocalDateTime.parse(startAt, TimeFormatterUtil.FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endAt, TimeFormatterUtil.FORMATTER);
        return new Time(start, end, this.timeZone, this.allDay);
    }

    public LocalDateTime convertToLocalDateTime(String time) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(time, TimeFormatterUtil.FORMATTER);
        return zonedDateTime.toLocalDateTime();
    }

    public static TimeGetResponse from(Time time) {
        return new TimeGetResponse(
            time.getStartAt().toString(),
            time.getEndAt().toString(),
            time.getTimeZone(),
            time.isAllDay()
        );
    }

}
