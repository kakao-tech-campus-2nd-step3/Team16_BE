package org.cookieandkakao.babting.domain.calendar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EventBriefDTO(

    @JsonProperty("id")
    String id,

    @JsonProperty("title")
    String title,

    @JsonProperty("type")
    String type,

    @JsonProperty("calendar_id")
    String calendarId,

    @JsonProperty("time")
    TimeDTO time,

    @JsonProperty("is_host")
    boolean isHost,

    @JsonProperty("is_recur_event")
    boolean isRecurEvent,

    @JsonProperty("color")
    String color
) {

}
