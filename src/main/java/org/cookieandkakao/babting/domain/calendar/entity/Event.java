package org.cookieandkakao.babting.domain.calendar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name= "event")
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

}