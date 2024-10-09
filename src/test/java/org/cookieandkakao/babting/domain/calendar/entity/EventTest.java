package org.cookieandkakao.babting.domain.calendar.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void eventCreationTest() {
        // Given
        PersonalCalendar personalCalendar = new PersonalCalendar(new Member(1L)); // Member ID는 예시입니다.
        Time time = new Time(LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Asia/Seoul", false);
        String kakaoEventId = "12345";
        String title = "Test Event";
        String description = "This is a test event";

        // When
        Event event = new Event(personalCalendar, time, null, kakaoEventId, title, false, null, null, description, null, null);

        // Then
        assertNotNull(event);
        assertEquals(personalCalendar, event.getPersonalCalendar());
        assertEquals(time, event.getTime());
        assertEquals(kakaoEventId, event.getKakaoEventId());
        assertEquals(title, event.getTitle());
        assertNull(event.getScheduleRepeatCycle());
        assertEquals(description, event.getDescription());
        assertEquals("USER", event.getType());
    }
}