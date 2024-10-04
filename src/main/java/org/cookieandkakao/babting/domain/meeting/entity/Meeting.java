package org.cookieandkakao.babting.domain.meeting.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "meeting")
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingId;

    @OneToOne
    @JoinColumn(name = "base_location_id")
    private Location baseLocation;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer durationTime;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column
    private LocalDateTime confirmDateTime;

    protected Meeting(){}

    public Meeting(Location baseLocation, String title, LocalDate startDate, LocalDate endDate,
        Integer durationTime, LocalTime startTime, LocalTime endTime) {
        this.baseLocation = baseLocation;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durationTime = durationTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void confirmDateTime(LocalDateTime confirmDateTime){
        this.confirmDateTime = confirmDateTime;
    }

    public LocalDateTime getConfirmDateTime() {
        return confirmDateTime;
    }
}
