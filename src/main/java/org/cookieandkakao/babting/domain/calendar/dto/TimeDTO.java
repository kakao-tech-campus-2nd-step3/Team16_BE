package org.cookieandkakao.babting.domain.calendar.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TimeDTO(

    String startAt,

    String endAt,

    String timeZone,

    boolean allDay
) {

}
