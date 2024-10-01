package org.cookieandkakao.babting.domain.calendar.dto.request;

public record EventCreateRequestDto(
    String title,

    TimeCreateRequestDto time,

    String rrule,

    String description
) {

}
