package org.cookieandkakao.babting.domain.calendar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "time")
public class Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timeId;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    private String timeZone;

    @Column(nullable = false)
    private boolean allDay;

    protected Time() {
    }

    public Time(LocalDateTime startAt, LocalDateTime endAt, String timeZone, boolean allDay) {
        this.startAt = startAt;
        this.endAt = endAt;
        this.timeZone = timeZone;
        this.allDay = allDay;
    }


}
