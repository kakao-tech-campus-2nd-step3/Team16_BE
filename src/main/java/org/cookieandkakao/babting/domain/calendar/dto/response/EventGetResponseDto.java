package org.cookieandkakao.babting.domain.calendar.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.cookieandkakao.babting.domain.calendar.entity.Reminder;
import org.cookieandkakao.babting.domain.meeting.entity.Location;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record EventGetResponseDto(

    String id,

    String title,

    String type,

    String calendarId,

    TimeGetResponseDto time,

    boolean isHost,

    boolean isRecurEvent,

    String rrule,

    String dtStart,

    String description,

    Location location,

    Reminder reminders,

    String color,

    String memo
) {

}
