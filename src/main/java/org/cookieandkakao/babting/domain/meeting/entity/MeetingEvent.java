package org.cookieandkakao.babting.domain.meeting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.cookieandkakao.babting.domain.calendar.entity.Event;

@Entity
@Table(name = "meeting_event")
public class MeetingEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingEventId;

    @ManyToOne
    @JoinColumn(name = "member_meeting_id")
    private MemberMeeting memberMeeting;

    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
