package org.cookieandkakao.babting.domain.calendar.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.calendar.exception.TimeNullException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EventTest {

    @Nested
    class 일정_생성자_성공_테스트 {

        @Test
        void 모든_필드가_있는_경우() {
            // Given
            Time time = new Time(LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                "Asia/Seoul",
                false);
            String kakaoEventId = "12345";
            String title = "Test Event";
            String description = "This is a test event";

            // When
            Event event = new Event(time, null, kakaoEventId, title, false, null,
                null, description, null, null);

            // Then
            assertNotNull(event);
            assertEquals(time, event.getTime());
            assertEquals(kakaoEventId, event.getKakaoEventId());
            assertEquals(title, event.getTitle());
            assertNull(event.getScheduleRepeatCycle());
            assertEquals(description, event.getDescription());
            assertEquals("USER", event.getType());
        }

        @Test
        void 시간_필드만_있는_경우() {
            // Given
            Time time = new Time(LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                "Asia/Seoul",
                false);

            // When
            Event event = new Event(time);

            // Then
            assertNotNull(event);
            assertEquals(time, event.getTime());
        }
    }

    @Nested
    class 일정_생성자_예외_테스트 {

        @Test
        void 모든_필드가_있지만_시간이_null인_경우() {
            // Given
            Time time = null;
            String title = "Test Event";

            // When
            Exception e = assertThrows(TimeNullException.class,
                () -> new Event(time, null, null, title, false, null, null, null, null, null));

            // Then
            assertEquals(e.getMessage(), "시간은 null이면 안됩니다.");
            assertEquals(e.getClass(), TimeNullException.class);
        }

        @Test
        void 시간_필드가_null인_경우() {
            // Given
            Time time = null;
            String title = "Test Event";

            // When
            Exception e = assertThrows(TimeNullException.class,
                () -> new Event(time));

            // Then
            assertEquals(e.getMessage(), "시간은 null이면 안됩니다.");
            assertEquals(e.getClass(), TimeNullException.class);
        }
    }
}