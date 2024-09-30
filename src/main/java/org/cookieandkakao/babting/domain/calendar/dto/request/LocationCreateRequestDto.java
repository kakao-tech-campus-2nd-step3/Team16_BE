package org.cookieandkakao.babting.domain.calendar.dto.request;

import org.cookieandkakao.babting.domain.meeting.entity.Location;

public record LocationCreateRequestDto(
    String name,
    String address,
    Double latitude,
    Double longitude
) {

    public Location toEntity() {
        return new Location(this.name, this.address, this.latitude, this.longitude);
    }

}