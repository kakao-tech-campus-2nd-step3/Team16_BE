package org.cookieandkakao.babting.domain.meeting.dto.response;

import java.util.List;
import org.cookieandkakao.babting.domain.calendar.dto.response.TimeGetResponse;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;

public record TimeAvailableGetResponse(
    String startDate,

    String endDate,
    Integer durationTime,

    List<TimeSlot> availableTime
) {
    public static TimeAvailableGetResponse of(Meeting meeting, List<TimeSlot> availableTime){
        return new TimeAvailableGetResponse(meeting.getStartDate().toString(), meeting.getEndDate().toString(),
            meeting.getDurationTime(), availableTime);
    }
}
