package org.cookieandkakao.babting.domain.meeting.dto.response;

import java.util.List;
import org.cookieandkakao.babting.domain.calendar.dto.response.TimeGetResponse;

public record TimeAvailableGetResponse(
    String startDate,

    String endDate,

    List<TimeGetResponse> availableTime
) {
}
