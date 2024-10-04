package org.cookieandkakao.babting.domain.calendar.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.cookieandkakao.babting.domain.calendar.entity.Event;
import org.cookieandkakao.babting.domain.calendar.entity.PersonalCalendar;
import org.cookieandkakao.babting.domain.calendar.entity.Reminder;
import org.cookieandkakao.babting.domain.calendar.entity.Time;
import org.cookieandkakao.babting.domain.meeting.entity.Location;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record EventGetResponse(

    String id,

    String title,

    String type,

    String calendarId,

    TimeGetResponse time,

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

    public Event toEntity(PersonalCalendar personalCalendar) {
        Time timeEntity = this.time.toEntity();
        return new Event(
            personalCalendar,
            timeEntity,
            this.location,
            this.id,
            this.title,
            this.isRecurEvent,
            this.rrule,
            this.dtStart,
            this.description,
            this.color,
            this.memo
        );
    }

}
