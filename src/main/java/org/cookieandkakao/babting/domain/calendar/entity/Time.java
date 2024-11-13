package org.cookieandkakao.babting.domain.calendar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.calendar.exception.InvalidTimeRangeException;

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
        validateTimeRange(startAt, endAt);
        this.startAt = startAt;
        this.endAt = endAt;
        this.timeZone = timeZone;
        this.allDay = allDay;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public boolean isAllDay() {
        return allDay;
    }

    private void validateTimeRange(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt.isAfter(endAt)) {
            throw new InvalidTimeRangeException("시작 시간이 종료 시간보다 늦을 수 없습니다.");
        }
    }
}
