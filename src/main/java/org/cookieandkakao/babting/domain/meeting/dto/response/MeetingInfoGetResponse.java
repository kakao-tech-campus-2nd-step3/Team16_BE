package org.cookieandkakao.babting.domain.meeting.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;

public record MeetingInfoGetResponse(
    String title,

    LocalDate startDate,

    LocalDate endDate,

    LocalTime startTime,

    LocalTime endTime
) {
    public static MeetingInfoGetResponse from(Meeting meeting) {
        return new MeetingInfoGetResponse(meeting.getTitle(), meeting.getStartDate(),
            meeting.getEndDate(),
            meeting.getStartTime(), meeting.getEndTime()
        );
    }
}
