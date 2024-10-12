package org.cookieandkakao.babting.domain.meeting.dto.response;

import org.cookieandkakao.babting.domain.meeting.entity.Location;

public record LocationGetResponse(
    Long locationId,

    String name,

    String address,

    Double latitude,

    Double longitude
){
    public static LocationGetResponse from(Location location) {
        return new LocationGetResponse(location.getLocationId(), location.getName(),
            location.getAddress(), location.getLatitude(), location.getLongitude());
    }
    public Location toEntity(){
        return new Location(name, address, latitude, longitude);

    }
}
