package org.cookieandkakao.babting.domain.meeting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import org.cookieandkakao.babting.domain.calendar.entity.Time;

@Entity
public class MeetingEvent {
    @Id
    @GeneratedValue
    private Long meetingEventId;

    @OneToOne
    private Time time;
}
