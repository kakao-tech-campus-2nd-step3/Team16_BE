package org.cookieandkakao.babting.domain.calendar.dto.request;

import java.util.List;


public record EventCreateRequestDto(
    String title,

    TimeCreateRequestDto time,

    String rrule,

    String description,

    LocationCreateRequestDto location,

    List<Long> reminders,

    String color
) {

}
