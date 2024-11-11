package org.cookieandkakao.babting.domain.calendar.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.TimeGetResponse;
import org.cookieandkakao.babting.domain.calendar.entity.Event;
import org.cookieandkakao.babting.domain.calendar.entity.Reminder;
import org.cookieandkakao.babting.domain.calendar.entity.Time;
import org.cookieandkakao.babting.domain.calendar.repository.EventRepository;
import org.cookieandkakao.babting.domain.calendar.repository.ReminderRepository;
import org.cookieandkakao.babting.domain.calendar.repository.TimeRepository;
import org.cookieandkakao.babting.domain.meeting.dto.response.LocationGetResponse;
import org.cookieandkakao.babting.domain.meeting.entity.Location;
import org.cookieandkakao.babting.domain.meeting.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

    // 메서드 인자 값 확인 위해 사용
    @Captor
    ArgumentCaptor<Event> eventCaptor;

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
        TimeGetResponse time = new TimeGetResponse("2024-10-01T00:00:00Z", "2024-10-01T03:00:00Z",
            "Asia/Seoul", false);
        LocationGetResponse location = new LocationGetResponse(null, "test", "test", 30.0, 32.2);

        // Mocking
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

        // 객체 내용 검증
        verify(eventRepository).save(eventCaptor.capture());
        Event event = eventCaptor.getValue();
        assertEquals("testTitle", event.getTitle());
        assertEquals("testDescription", event.getDescription());
        assertEquals("testId", event.getKakaoEventId());
        assertNull(event.getScheduleRepeatCycle());
    }

    @Test
    void saveEventTest_LocationSaveFailed() {
        // Given
        Long memberId = 1L;
        EventGetResponse eventGetResponseMock = mock(EventGetResponse.class);
        TimeGetResponse time = new TimeGetResponse("2024-10-01T00:00:00Z", "2024-10-01T03:00:00Z",
            "Asia/Seoul", false);
        LocationGetResponse location = new LocationGetResponse(null, "test", "test", 30.0, 32.2);

        // Mocking
        given(eventGetResponseMock.time()).willReturn(time);
        given(eventGetResponseMock.location()).willReturn(location);
        given(locationRepository.save(any(Location.class))).willThrow(
            new IllegalArgumentException("위치 저장 실패"));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> eventService.saveEvent(eventGetResponseMock, memberId));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals("위치 저장 실패", e.getMessage());
        verify(timeRepository).save(any(Time.class));
        verify(locationRepository).save(any(Location.class));
        verify(eventRepository, times(0)).save(any(Event.class));
    }

    @Test
    void saveEventTest_EventSaveFailed() {
        // Given
        Long memberId = 1L;
        EventGetResponse eventGetResponseMock = mock(EventGetResponse.class);
        TimeGetResponse time = new TimeGetResponse("2024-10-01T00:00:00Z", "2024-10-01T03:00:00Z",
            "Asia/Seoul", false);
        LocationGetResponse location = new LocationGetResponse(null, "test", "test", 30.0, 32.2);

        // Mocking
        given(eventGetResponseMock.time()).willReturn(time);
        given(eventGetResponseMock.location()).willReturn(location);
        given(eventRepository.save(any(Event.class))).willThrow(
            new IllegalArgumentException("일정 저장 실패"));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> eventService.saveEvent(eventGetResponseMock, memberId));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(), "일정 저장 실패");
        verify(timeRepository).save(any(Time.class));
        verify(locationRepository).save(any(Location.class));
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void saveEventTest_ReminderSaveFailed() {
        // Given
        Long memberId = 1L;
        EventGetResponse eventGetResponseMock = mock(EventGetResponse.class);
        TimeGetResponse time = new TimeGetResponse("2024-10-01T00:00:00Z", "2024-10-01T03:00:00Z",
            "Asia/Seoul", false);

        // Mocking
        given(eventGetResponseMock.time()).willReturn(time);
        given(eventGetResponseMock.reminders()).willReturn(List.of(5, 10));
        given(reminderRepository.save(any(Reminder.class))).willThrow(
            new IllegalArgumentException("리마인더 저장 실패"));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> eventService.saveEvent(eventGetResponseMock, memberId));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(), "리마인더 저장 실패");
        verify(timeRepository).save(any(Time.class));
        verify(locationRepository, times(0)).save(any(Location.class));
        verify(eventRepository).save(any(Event.class));
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void saveEventTest_TimeSaveFailed() {
        // Given
        Long memberId = 1L;
        EventGetResponse eventGetResponseMock = mock(EventGetResponse.class);
        TimeGetResponse time = new TimeGetResponse("2024-10-01T00:00:00Z", "2024-10-01T03:00:00Z",
            "Asia/Seoul", false);

        // Mocking
        given(eventGetResponseMock.time()).willReturn(time);
        given(timeRepository.save(any(Time.class))).willThrow(
            new IllegalArgumentException("시간 저장 실패"));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> eventService.saveEvent(eventGetResponseMock, memberId));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals("시간 저장 실패", e.getMessage());
        verify(timeRepository).save(any(Time.class));
        verify(eventRepository, times(0)).save(any(Event.class));
    }

    @Test
    void saveAvoidTimeEventTest() {
        // Given
        LocalDateTime startAt = LocalDateTime.of(2024, 11, 6, 10, 0);
        LocalDateTime endAt = LocalDateTime.of(2024, 11, 6, 12, 0);
        Time avoidTime = new Time(startAt, endAt, "Asia/Seoul", false);
        Event avoidTimeEvent = new Event(avoidTime);

        // Mocking
        given(eventRepository.save(any(Event.class))).willReturn(avoidTimeEvent);

        // When
        Event result = eventService.saveAvoidTimeEvent(avoidTime);

        // Then
        verify(eventRepository).save(any(Event.class));
        assertNotNull(result);
        assertEquals(avoidTime, result.getTime());
        assertEquals(result.getTime().getStartAt(), avoidTimeEvent.getTime().getStartAt());
        assertEquals(result.getTime().getEndAt(), avoidTimeEvent.getTime().getEndAt());
        assertEquals(result.getTime().getTimeZone(), avoidTimeEvent.getTime().getTimeZone());
        assertEquals(result.getTime().isAllDay(), avoidTimeEvent.getTime().isAllDay());
    }

    @Test
    void saveAvoidTimeEventTest_EventSaveFailed() {
        // Given
        LocalDateTime startAt = LocalDateTime.of(2024, 11, 6, 10, 0);
        LocalDateTime endAt = LocalDateTime.of(2024, 11, 6, 12, 0);
        Time avoidTime = new Time(startAt, endAt, "Asia/Seoul", false);
        Event avoidTimeEvent = new Event(avoidTime);

        // Mocking
        given(eventRepository.save(any(Event.class))).willThrow(new IllegalArgumentException("일정 저장 실패"));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> eventService.saveAvoidTimeEvent(avoidTime));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(), "일정 저장 실패");
        verify(eventRepository).save(any(Event.class));
    }

}
