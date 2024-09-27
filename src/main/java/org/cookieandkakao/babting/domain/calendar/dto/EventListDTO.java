package org.cookieandkakao.babting.domain.calendar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record EventListDTO(

    int status,

    String message,

    List<EventBriefDTO> events,

    @JsonProperty("has_next")
    boolean hasNext
) {

}
