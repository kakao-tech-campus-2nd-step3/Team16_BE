package org.cookieandkakao.babting.domain.meeting.dto.response;

public record LocationCreateResponse(
    Long location_id,

    String name,

    String address,

    Double latitude,

    Double longitude
){
}
