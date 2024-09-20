package org.cookieandkakao.babting.domain.calendar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "time")
public class Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timeId;

    @Column(nullable = false)
    private ZonedDateTime startAt;

    @Column(nullable = false)
    private ZonedDateTime endAt;

    @Column(nullable = false)
    private String timeZone;

    @Column(nullable = false)
    private boolean allDay;

}
