package org.cookieandkakao.babting.domain.meeting.dto.response;

import java.util.List;
import org.cookieandkakao.babting.domain.calendar.dto.response.TimeGetResponse;

public record MeetingPersonalEventGetResponse(
    List<TimeGetResponse> meetingPersonalTimes
) {

}
