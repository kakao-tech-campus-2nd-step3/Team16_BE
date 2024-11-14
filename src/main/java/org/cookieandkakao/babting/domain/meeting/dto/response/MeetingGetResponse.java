package org.cookieandkakao.babting.domain.meeting.dto.response;

import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.food.dto.FoodGetResponse;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;

public record MeetingGetResponse(
    Long meetingId,
    LocationGetResponse baseLocation,

    String title,

    LocalDateTime confirmedDateTime,

    FoodGetResponse confirmedFood
) {
    public static MeetingGetResponse from(Meeting meeting) {
        if (meeting.getConfirmedFood() == null){
            return new MeetingGetResponse(meeting.getMeetingId(),
                LocationGetResponse.from(meeting.getBaseLocation()),
                meeting.getTitle(),
                meeting.getConfirmDateTime(),
                FoodGetResponse.from(null));
        }
        return new MeetingGetResponse(meeting.getMeetingId(),
            LocationGetResponse.from(meeting.getBaseLocation()),
            meeting.getTitle(),
            meeting.getConfirmDateTime(),
            FoodGetResponse.from(meeting.getConfirmedFood()));
    }
}
