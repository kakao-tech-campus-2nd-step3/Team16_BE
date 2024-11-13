package org.cookieandkakao.babting.domain.calendar.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.calendar.exception.InvalidTimeRangeException;
import org.junit.jupiter.api.Test;

class TimeTest {

    @Test
    void 시간_생성자_테스트() {
        //Given
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = startAt.plusHours(1);
        String timeZone = "Asia/Seoul";
        boolean allDay = false;

        // When
        Time time = new Time(startAt, endAt, timeZone, allDay);

        // Then
        assertNotNull(time);
        assertEquals(timeZone, time.getTimeZone());
        assertEquals(startAt, time.getStartAt());
        assertEquals(endAt, time.getEndAt());
        assertEquals(allDay, time.isAllDay());
    }

    @Test
    void 시간_생성자_예외_테스트_startAt이_endAt_보다_늦을_때() {
        // Given
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = startAt.minusHours(1);
        String timeZone = "Asia/Seoul";
        boolean allDay = false;

        // When
        Exception e = assertThrows(InvalidTimeRangeException.class,
            () -> new Time(startAt, endAt, timeZone, allDay));

        // Then
        assertEquals(e.getMessage(), "시작 시간이 종료 시간보다 늦을 수 없습니다.");
        assertEquals(e.getClass(), InvalidTimeRangeException.class);
    }
}