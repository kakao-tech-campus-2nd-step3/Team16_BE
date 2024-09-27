package org.cookieandkakao.babting.domain.calendar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record EventListGetResponseDto(

    int status,

    String message,

    List<EventGetResponseDto> events,

    @JsonProperty("has_next")
    boolean hasNext
) {

}
