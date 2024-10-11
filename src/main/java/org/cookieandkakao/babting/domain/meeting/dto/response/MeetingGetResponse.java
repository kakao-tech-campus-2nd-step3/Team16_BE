package org.cookieandkakao.babting.domain.meeting.dto.response;

import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.food.dto.FoodGetResponse;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;

public record MeetingGetResponse(
    LocationGetResponse baseLocation,

    String title,

    LocalDateTime confirmedDateTime,

    FoodGetResponse confirmedFood
) {
    public static MeetingGetResponse from(Meeting meeting) {
        return new MeetingGetResponse(LocationGetResponse.from(meeting.getBaseLocation()),
            meeting.getTitle(),
            meeting.getConfirmDateTime(),
            FoodGetResponse.from(meeting.getConfirmedFood()));
    }
}
