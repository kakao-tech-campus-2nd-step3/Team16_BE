package org.cookieandkakao.babting.domain.meeting.dto.response;

import org.cookieandkakao.babting.domain.food.dto.FoodGetResponse;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;

public record MeetingConfirmedInfoResponse(
    String confirmedDateTime,
    FoodGetResponse confirmedFood,
    LocationGetResponse baseLocation
) {
    public static MeetingConfirmedInfoResponse from(Meeting meeting) {
        return new MeetingConfirmedInfoResponse(meeting.getConfirmDateTime().toString(),
            FoodGetResponse.from(meeting.getConfirmedFood()), LocationGetResponse.from(meeting.getBaseLocation()));
    }
}
