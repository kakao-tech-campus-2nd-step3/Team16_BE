package org.cookieandkakao.babting.domain.calendar.dto.request;

public record EventCreateRequest(
    String title,

    TimeCreateRequest time,

    String rrule,

    String description
) {

}
