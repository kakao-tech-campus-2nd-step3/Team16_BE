package org.cookieandkakao.babting.domain.meeting.service;

import jakarta.transaction.Transactional;
import org.cookieandkakao.babting.domain.meeting.repository.LocationRepository;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingEventRepository;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingRepository;
import org.cookieandkakao.babting.domain.meeting.repository.MemberMeetingRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final MeetingEventRepository meetingEventRepository;
    private final MemberMeetingRepository memberMeetingRepository;
    private final LocationRepository locationRepository;

    public MeetingService(MeetingRepository meetingRepository, MeetingEventRepository meetingEventRepository,
        MemberMeetingRepository memberMeetingRepository, LocationRepository locationRepository) {
        this.meetingRepository = meetingRepository;
        this.meetingEventRepository = meetingEventRepository;
        this.memberMeetingRepository = memberMeetingRepository;
        this.locationRepository = locationRepository;
    }

    // 모임 생성(주최자)
    public void createMeeting(){}
    // 모임 시간, 장소 확정(주최자)
    public void decideMeetingTimeAndLocation(){}
    // 모임 참가(초대받은사람)
    public void joinMeeting(){}
    // 모임 탈퇴(주최자, 초대받은 사람)
    public void exitMeeting(){}
}
