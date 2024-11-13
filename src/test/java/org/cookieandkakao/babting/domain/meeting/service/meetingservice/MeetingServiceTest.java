package org.cookieandkakao.babting.domain.meeting.service.meetingservice;

import java.time.LocalDate;
import java.time.LocalTime;
import org.cookieandkakao.babting.domain.meeting.dto.request.LocationCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingCreateRequest;
import org.cookieandkakao.babting.domain.meeting.repository.LocationRepository;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingRepository;
import org.cookieandkakao.babting.domain.meeting.repository.MemberMeetingRepository;
import org.cookieandkakao.babting.domain.meeting.service.MeetingEventCreateService;
import org.cookieandkakao.babting.domain.meeting.service.MeetingService;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
abstract class MeetingServiceTest {
    @Mock
    protected MeetingRepository meetingRepository;
    @Mock
    protected MemberMeetingRepository memberMeetingRepository;
    @Mock
    protected LocationRepository locationRepository;
    @Mock
    protected MemberService memberService;
    @Mock
    protected MeetingEventCreateService meetingEventCreateService;
    @InjectMocks
    protected MeetingService meetingService;

    protected LocationCreateRequest baseLocation;
    protected MeetingCreateRequest meetingCreateRequest;

    @BeforeEach
    void setUp(){
        baseLocation = new LocationCreateRequest("전대", "11", 1.1, 1.1);
        meetingCreateRequest = new MeetingCreateRequest(baseLocation, "밥팅",
            LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), 3, LocalTime.of(14, 0),
            LocalTime.of(17, 0));
    }
}
