package org.cookieandkakao.babting.domain.calendar.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.cookieandkakao.babting.domain.member.entity.Member;

@Entity
@Table(name = "personal_calendar")
public class PersonalCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personalCalendarId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


}
