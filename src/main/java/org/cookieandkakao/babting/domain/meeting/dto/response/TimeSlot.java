package org.cookieandkakao.babting.domain.meeting.dto.response;

import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.calendar.dto.response.TimeGetResponse;

public record TimeSlot(
    LocalDateTime startAt,
    LocalDateTime endAt,
    String timeZone,
    boolean allDay
) {
    public static TimeSlot toTimeSlot(TimeGetResponse timeGetResponse){
        return new TimeSlot(timeGetResponse.convertToLocalDateTime(timeGetResponse.startAt()),
            timeGetResponse.convertToLocalDateTime(timeGetResponse.endAt()),
            timeGetResponse.timeZone(), timeGetResponse.allDay()
        );
    }
}