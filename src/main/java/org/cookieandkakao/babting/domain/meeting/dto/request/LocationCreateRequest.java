package org.cookieandkakao.babting.domain.meeting.dto.request;

import jakarta.validation.constraints.NotNull;
import org.cookieandkakao.babting.domain.meeting.entity.Location;

public record LocationCreateRequest(
    @NotNull
    String name,

    @NotNull
    String address,

    @NotNull
    Double latitude,

    @NotNull
    Double longitude
) {
    public Location toEntity(){
        return new Location(name, address, latitude, longitude);
    }
}
