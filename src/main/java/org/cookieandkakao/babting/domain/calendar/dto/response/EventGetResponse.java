package org.cookieandkakao.babting.domain.calendar.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import org.cookieandkakao.babting.domain.calendar.entity.Event;
import org.cookieandkakao.babting.domain.calendar.entity.Time;
import org.cookieandkakao.babting.domain.meeting.dto.response.LocationGetResponse;
import org.cookieandkakao.babting.domain.meeting.entity.Location;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record EventGetResponse(

    String id,

    String title,

    String type,

    TimeGetResponse time,

    boolean isHost,

    boolean isRecurEvent,

    String rrule,

    String dtStart,

    String description,

    LocationGetResponse location,

    List<Integer> reminders,

    String color,

    String memo
) {

    public Event toEntity() {
        Time timeEntity = this.time.toEntity();
        Location locationEntity = this.location.toEntity();
        return new Event(
            timeEntity,
            locationEntity,
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
