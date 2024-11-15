package org.cookieandkakao.babting.domain.meeting.entity;

import static jakarta.persistence.CascadeType.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import org.cookieandkakao.babting.domain.food.entity.MeetingNonPreferenceFood;
import org.cookieandkakao.babting.domain.food.entity.MeetingPreferenceFood;
import org.cookieandkakao.babting.domain.member.entity.Member;

@Entity
@Table(name = "member_meeting")
public class MemberMeeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberMeetingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @OneToMany(mappedBy = "memberMeeting", cascade = REMOVE, orphanRemoval = true)
    private List<MeetingEvent> meetingEvents;

    @OneToMany(mappedBy = "memberMeeting", cascade = REMOVE, orphanRemoval = true)
    private List<MeetingPreferenceFood> meetingPreferenceFoods;

    @OneToMany(mappedBy = "memberMeeting", cascade = REMOVE, orphanRemoval = true)
    private List<MeetingNonPreferenceFood> meetingNonPreferenceFoods;

    private boolean isHost;

    protected MemberMeeting(){}

    public MemberMeeting(Member member, Meeting meeting, boolean isHost) {
        this.member = member;
        this.meeting = meeting;
        this.isHost = isHost;
    }
    public Member getMember() {
        return member;
    }

    public boolean isHost() {
        return isHost;
    }

    public Meeting getMeeting() {
        return meeting;
    }
}
