package org.cookieandkakao.babting.domain.calendar.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.cookieandkakao.babting.domain.member.entity.Member;
import org.junit.jupiter.api.Test;

class PersonalCalendarTest {

    @Test
    void personalCalendarCreateTest() {
        // Given
        Member member = new Member(1L); // Member ID는 예시입니다.

        // When
        PersonalCalendar personalCalendar = new PersonalCalendar(member);

        // Then
        assertNotNull(personalCalendar);
        assertEquals(member, personalCalendar.getMember());
    }

}