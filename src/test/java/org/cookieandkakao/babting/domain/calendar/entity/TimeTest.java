package org.cookieandkakao.babting.domain.calendar.entity;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class TimeTest {

    @Test
    void timeCreateTest() {
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
}