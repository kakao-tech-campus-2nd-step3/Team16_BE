package org.cookieandkakao.babting.domain.meeting.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import org.cookieandkakao.babting.common.util.TimeFormatterUtil;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.TimeGetResponse;
import org.cookieandkakao.babting.domain.calendar.service.TalkCalendarService;
import org.cookieandkakao.babting.domain.meeting.dto.response.MeetingPersonalEventGetResponse;
import org.cookieandkakao.babting.domain.meeting.dto.response.TimeAvailableGetResponse;
import org.cookieandkakao.babting.domain.meeting.dto.response.TimeSlot;
import org.cookieandkakao.babting.domain.meeting.entity.Location;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.TimeZone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MeetingTimeCalculationServiceTest {

    @Mock
    private MeetingService meetingService;
    @Mock
    private TalkCalendarService talkCalendarService;
    @Mock
    private MeetingEventService meetingEventService;
    @InjectMocks
    private MeetingTimeCalculationService meetingTimeCalculationService;

    @Test
    void 시간_파싱(){
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2024-10-24T12:00:00+09:00", TimeFormatterUtil.FORMATTER);
        System.out.println(zonedDateTime);
    }

    @Test
    void 빈_시간_계산_성공() {
        // Given
        Long meetingId = 1L;
        Long memberId1 = 1L;
        Long memberId2 = 2L;

        Location baseLocation = mock(Location.class);
        LocalDate startDate = LocalDate.of(2024, 10, 24);
        LocalDate endDate = LocalDate.of(2024, 10, 24);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(18, 0);
        Meeting meeting = new Meeting(baseLocation, "밥팅", startDate, endDate, 8, startTime,
            endTime);

        List<Long> joinedMemberIds = List.of(memberId1, memberId2);

        List<TimeGetResponse> member1Times = List.of(
            new TimeGetResponse("2024-10-24T12:00:00+09:00", "2024-10-24T14:00:00+09:00", TimeZone.SEOUL.getArea(), false)
        );

        List<TimeGetResponse> member2Times = List.of(
            new TimeGetResponse("2024-10-24T13:00:00+09:00", "2024-10-24T15:00:00+09:00", TimeZone.SEOUL.getArea(), false)
        );

        List<TimeGetResponse> personalMeetingTimes1 = List.of(
            new TimeGetResponse("2024-10-24T15:30:00+09:00", "2024-10-24T16:30:00+09:00", TimeZone.SEOUL.getArea(), false)
        );
        List<TimeGetResponse> personalMeetingTimes2 = List.of(
            new TimeGetResponse("2024-10-24T16:00:00+09:00", "2024-10-24T17:00:00+09:00", TimeZone.SEOUL.getArea(), false)
        );

        when(meetingService.getMemberIdInMeetingId(meetingId)).thenReturn(joinedMemberIds);
        when(meetingService.findMeeting(meetingId)).thenReturn(meeting);
        when(talkCalendarService.getUpdatedEventList(anyString(), anyString(), eq(memberId1)))
            .thenReturn(List.of(
                new EventGetResponse(
                    null, null, null,
                    member1Times.getFirst(),
                    false, true,
                    null, null, null, null,
                    null, null, null
                )
            ));

        when(talkCalendarService.getUpdatedEventList(anyString(), anyString(), eq(memberId2)))
            .thenReturn(List.of(
                new EventGetResponse(
                    null, null, null,
                    member2Times.getFirst(),
                    false, true,
                    null, null, null, null,
                    null, null, null
                )
            ));

        when(meetingEventService.findMeetingPersonalEvent(meetingId, memberId1))
            .thenReturn(new MeetingPersonalEventGetResponse(personalMeetingTimes1));
        when(meetingEventService.findMeetingPersonalEvent(meetingId, memberId2))
            .thenReturn(new MeetingPersonalEventGetResponse(personalMeetingTimes2));


        // When
        TimeAvailableGetResponse result = meetingTimeCalculationService.findAvailableTime(
            meetingId);

        // Then
        List<TimeSlot> expectedAvailableTimes = List.of(
            new TimeSlot(LocalDateTime.of(2024, 10, 24, 10, 0),
                LocalDateTime.of(2024, 10, 24, 12, 0),
                TimeZone.SEOUL.getArea(), false),
            new TimeSlot(LocalDateTime.of(2024, 10, 24, 15, 0),
                LocalDateTime.of(2024, 10, 24, 15, 30),
                TimeZone.SEOUL.getArea(), false),
            new TimeSlot(LocalDateTime.of(2024, 10, 24, 17, 00),
                LocalDateTime.of(2024, 10, 24, 18, 00),
                TimeZone.SEOUL.getArea(), false)
        );

        assertEquals(expectedAvailableTimes, result.availableTime());
    }
}
