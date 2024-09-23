package org.cookieandkakao.babting.domain.meeting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import org.cookieandkakao.babting.domain.calendar.entity.Time;

@Entity
public class MeetingEvent {
    @Id
    @GeneratedValue
    private Long meetingEventId;

    @ManyToOne
    @JoinColumn(name = "member_meeting_id")
    private MemberMeeting memberMeeting;

    @OneToOne
    @JoinColumn(name = "time_id")
    private Time time;
}
