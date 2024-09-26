package org.cookieandkakao.babting.domain.calendar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TimeDTO(

    @JsonProperty("start_at")
    String startAt,

    @JsonProperty("end_at")
    String endAt,

    @JsonProperty("time_zone")
    String timeZone,

    @JsonProperty("all_day")
    boolean allDay
) {

}
