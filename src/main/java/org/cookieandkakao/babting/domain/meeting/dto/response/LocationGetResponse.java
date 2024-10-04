package org.cookieandkakao.babting.domain.meeting.dto.response;

public record LocationGetResponse(
    Long locationId,

    String name,

    String address,

    Double latitude,

    Double longitude
){
}
