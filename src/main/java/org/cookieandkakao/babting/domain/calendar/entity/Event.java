package org.cookieandkakao.babting.domain.calendar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.cookieandkakao.babting.domain.meeting.entity.Location;

@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne
    @JoinColumn(name = "personal_calendar_id", nullable = false)
    private PersonalCalendar personalCalendar;

    @OneToOne
    @JoinColumn(name = "time_id", nullable = false)
    private Time time;

    @OneToOne
    @JoinColumn(name = "location")
    private Location location;

    @Column
    private String kakaoEventId;

    @Column
    private String title;

    @Column
    private String type;

    @Column
    private boolean repeatedSchedule;

    @Column
    private String scheduleRepeatCycle;

    @Column
    private String dtStart;

    @Column
    private String description;

    @Column
    private String eventColor;

    @Column
    private String memo;

    protected Event() {
    }

    public Event(PersonalCalendar personalCalendar, Time time, Location location,
        String kakaoEventId, String title, boolean repeatedSchedule,
        String scheduleRepeatCycle, String dtStart, String description, String eventColor,
        String memo) {
        this.personalCalendar = personalCalendar;
        this.time = time;
        this.location = location;
        this.kakaoEventId = kakaoEventId;
        this.title = title;
        this.type = "USER";
        this.repeatedSchedule = repeatedSchedule;
        this.scheduleRepeatCycle = scheduleRepeatCycle;
        this.dtStart = dtStart;
        this.description = description;
        this.eventColor = eventColor;
        this.memo = memo;
    }

}