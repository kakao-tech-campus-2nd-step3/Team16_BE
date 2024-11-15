package org.cookieandkakao.babting.domain.meeting.dto.response;

import org.cookieandkakao.babting.domain.food.dto.FoodGetResponse;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;

public record MeetingConfirmedInfoResponse(
    String confirmedDateTime,
    FoodGetResponse confirmedFood,
    LocationGetResponse baseLocation
) {
    public static MeetingConfirmedInfoResponse from(Meeting meeting) {
        String confirmDateTime = null;
        if (meeting.getConfirmDateTime() != null) {
            confirmDateTime = meeting.getConfirmDateTime().toString();
        }

        FoodGetResponse foodGetResponse = null;
        if (meeting.getConfirmedFood() != null) {
            foodGetResponse = FoodGetResponse.from(meeting.getConfirmedFood());
        }

        return new MeetingConfirmedInfoResponse(
            confirmDateTime,
            foodGetResponse,
            LocationGetResponse.from(meeting.getBaseLocation())
        );
    }
}
