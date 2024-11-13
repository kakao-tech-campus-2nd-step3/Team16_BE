package org.cookieandkakao.babting.domain.calendar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.calendar.entity.Event;
import org.cookieandkakao.babting.domain.calendar.entity.Time;
import org.cookieandkakao.babting.domain.calendar.repository.EventRepository;
import org.cookieandkakao.babting.domain.calendar.repository.TimeRepository;
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

    // Mock 객체 초기화
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 피하고_싶은_시간_일정_저장_테스트() {
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
    }

    @Test
    void 피하고_싶은_시간_일정_저장_테스트_실패_예외() {
        // Given
        LocalDateTime startAt = LocalDateTime.of(2024, 11, 6, 10, 0);
        LocalDateTime endAt = LocalDateTime.of(2024, 11, 6, 12, 0);
        Time avoidTime = new Time(startAt, endAt, "Asia/Seoul", false);
        Event avoidTimeEvent = new Event(avoidTime);

        // Mocking
        given(eventRepository.save(any(Event.class))).willThrow(
            new IllegalArgumentException("일정 저장 실패"));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> eventService.saveAvoidTimeEvent(avoidTime));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(), "일정 저장 실패");
        verify(eventRepository).save(any(Event.class));
    }

}
