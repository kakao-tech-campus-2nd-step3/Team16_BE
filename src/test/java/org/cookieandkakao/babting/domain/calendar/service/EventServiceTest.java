package org.cookieandkakao.babting.domain.calendar.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.cookieandkakao.babting.domain.calendar.dto.request.EventCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.request.TimeCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.TimeGetResponse;
import org.cookieandkakao.babting.domain.calendar.entity.Event;
import org.cookieandkakao.babting.domain.calendar.entity.PersonalCalendar;
import org.cookieandkakao.babting.domain.calendar.entity.Reminder;
import org.cookieandkakao.babting.domain.calendar.entity.Time;
import org.cookieandkakao.babting.domain.calendar.repository.EventRepository;
import org.cookieandkakao.babting.domain.calendar.repository.PersonalCalendarRepository;
import org.cookieandkakao.babting.domain.calendar.repository.ReminderRepository;
import org.cookieandkakao.babting.domain.calendar.repository.TimeRepository;
import org.cookieandkakao.babting.domain.meeting.dto.response.LocationGetResponse;
import org.cookieandkakao.babting.domain.meeting.entity.Location;
import org.cookieandkakao.babting.domain.meeting.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TimeRepository timeRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private PersonalCalendarRepository personalCalendarRepository;

    @Mock
    private PersonalCalendarService personalCalendarService;

    // Mock 객체 초기화
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveEventTest() {
        // Given
        Long memberId = 1L;
        // EventGetResponse만 Mock 사용한 이유 : 저장하기 위해 EventGetResponse를 사용해서 저장을 모방하기 위해 사용
        EventGetResponse eventGetResponseMock = mock(EventGetResponse.class);
        PersonalCalendar personalCalendar = new PersonalCalendar(null);
        TimeGetResponse time = new TimeGetResponse("2024-10-01T00:00:00Z", "2024-10-01T03:00:00Z",
            "Asia/Seoul", false);
        LocationGetResponse location = new LocationGetResponse(null, "test", "test", 30.0, 32.2);

        // Mocking
        given(personalCalendarService.findOrCreatePersonalCalendar(memberId)).willReturn(
            personalCalendar);
        given(eventGetResponseMock.time()).willReturn(time);
        given(eventGetResponseMock.location()).willReturn(location);
        given(eventGetResponseMock.id()).willReturn("testId");
        given(eventGetResponseMock.title()).willReturn("testTitle");
        given(eventGetResponseMock.isRecurEvent()).willReturn(false);
        given(eventGetResponseMock.rrule()).willReturn(null);
        given(eventGetResponseMock.dtStart()).willReturn(null);
        given(eventGetResponseMock.description()).willReturn("testDescription");
        given(eventGetResponseMock.color()).willReturn("testColor");
        given(eventGetResponseMock.memo()).willReturn("testMemo");

        // When
        eventService.saveEvent(eventGetResponseMock, memberId);

        // Then
        verify(timeRepository).save(any(Time.class));
        verify(locationRepository).save(any(Location.class));
        verify(eventRepository).save(any(Event.class));
        verify(reminderRepository, times(0)).save(any(Reminder.class));
    }

    @Test
    void saveCreatedEventTest() {
        // Given
        Long memberId = 1L;
        String eventId = "testId";
        EventCreateRequest eventCreateRequestMock = mock(EventCreateRequest.class);
        PersonalCalendar personalCalendar = new PersonalCalendar(null);
        TimeCreateRequest time = new TimeCreateRequest("2024-10-01T00:00:00Z",
            "2024-10-01T03:00:00Z", "Asia/Seoul", false);

        // Mocking
        given(personalCalendarService.findOrCreatePersonalCalendar(memberId)).willReturn(
            personalCalendar);
        given(eventCreateRequestMock.time()).willReturn(time);
        given(eventCreateRequestMock.title()).willReturn("testTitle");
        given(eventCreateRequestMock.rrule()).willReturn(null);
        given(eventCreateRequestMock.description()).willReturn("testDescription");

        // When
        eventService.saveCreatedEvent(eventCreateRequestMock, eventId, memberId);

        // Then
        verify(timeRepository).save(any(Time.class));
        verify(eventRepository).save(any(Event.class));

    }

}
