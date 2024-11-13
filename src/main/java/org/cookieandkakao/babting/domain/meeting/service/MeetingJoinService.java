package org.cookieandkakao.babting.domain.meeting.service;

import org.cookieandkakao.babting.domain.food.service.MeetingFoodPreferenceUpdater;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingJoinCreateRequest;
import org.springframework.stereotype.Service;

@Service
public class MeetingJoinService {

    private final MeetingService meetingService;
    private final MeetingFoodPreferenceUpdater meetingFoodPreferenceUpdater;


    public MeetingJoinService(MeetingService meetingService,
        MeetingFoodPreferenceUpdater meetingFoodPreferenceUpdater) {
        this.meetingService = meetingService;
        this.meetingFoodPreferenceUpdater = meetingFoodPreferenceUpdater;
    }

    public void joinMeeting(Long memberId, Long meetingId,
        MeetingJoinCreateRequest meetingJoinCreateRequest) {
        meetingService.joinMeeting(memberId, meetingId, meetingJoinCreateRequest);
        meetingFoodPreferenceUpdater.updatePreferences(meetingId, memberId,
            meetingJoinCreateRequest.preferences(), meetingJoinCreateRequest.nonPreferences());
    }
}
