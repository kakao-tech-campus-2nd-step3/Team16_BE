package org.cookieandkakao.babting.domain.calendar.dto.response;

import java.util.List;

public record EventListGetResponse(
    List<EventGetResponse> events
) {

}
