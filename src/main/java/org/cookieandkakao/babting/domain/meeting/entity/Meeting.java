package org.cookieandkakao.babting.domain.meeting.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.cookieandkakao.babting.domain.food.entity.Food;

@Entity
@Table(name = "meeting")
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingId;

    @OneToOne
    @JoinColumn(name = "base_location_id")
    private Location baseLocation;

    @OneToOne
    @JoinColumn(name = "confirmed_food")
    private Food confirmedFood;

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
    private LocalDateTime confirmedDateTime;

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

    public void confirmDateTime(LocalDateTime confirmedDateTime){
        this.confirmedDateTime = confirmedDateTime;
    }

    public void confirmFood (Food food) {
        this.confirmedFood = food;
    }

    public LocalDateTime getConfirmDateTime() {
        return confirmedDateTime;
    }

    public Location getBaseLocation() {
        return baseLocation;
    }

    public String getTitle() {
        return title;
    }

    public Food getConfirmedFood() {
        return confirmedFood;
    }

    public Integer getDurationTime() {
        return durationTime;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void updateBaseLocation(Location baseLocation) {
        this.baseLocation = baseLocation;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void updateEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void updateDurationTime(Integer durationTime) {
        this.durationTime = durationTime;
    }

    public void updateStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void updateEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
