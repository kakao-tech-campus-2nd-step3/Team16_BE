package org.cookieandkakao.babting.domain.food.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.cookieandkakao.babting.domain.meeting.entity.*;

@Entity
@Table(name = "meeting_preference_food")
public class MeetingPreferenceFood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingPreferenceFoodId;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @ManyToOne
    @JoinColumn(name = "member_meeting_id", nullable = false)
    private MemberMeeting memberMeeting;

    protected MeetingPreferenceFood() {}

    public MeetingPreferenceFood(Food food, MemberMeeting memberMeeting) {
        this.food = food;
        this.memberMeeting = memberMeeting;
    }

    public Food getFood() {
        return food;
    }
}
