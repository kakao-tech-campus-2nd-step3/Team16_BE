package org.cookieandkakao.babting.domain.calendar.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record EventBriefDTO(

    String id,

    String title,

    String type,

    String calendarId,

    TimeDTO time,

    boolean isHost,

    boolean isRecurEvent,

    String color
) {

}
