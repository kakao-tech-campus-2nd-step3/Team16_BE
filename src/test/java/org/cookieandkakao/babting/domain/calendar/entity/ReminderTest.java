package org.cookieandkakao.babting.domain.calendar.entity;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.junit.jupiter.api.Test;

class ReminderTest {

    @Test
    void reminderCreateTest() {
        // Given
        Event event = new Event(new PersonalCalendar(new Member(1L)),
            new Time(LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Asia/Seoul", false),
            null, "12345", "Test Event", false, null, null, null, null, null);
        Long remindTime = 60L;

        // When
        Reminder reminder = new Reminder(event, remindTime);

        // Then
        assertNotNull(reminder);
        assertEquals(event, reminder.getEvent());
        assertEquals(remindTime, reminder.getRemindTime());
    }

}