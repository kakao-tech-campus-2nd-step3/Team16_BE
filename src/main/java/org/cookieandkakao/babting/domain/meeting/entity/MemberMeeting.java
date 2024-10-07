package org.cookieandkakao.babting.domain.meeting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    private boolean isHost;

    protected MemberMeeting(){}

    public MemberMeeting(Member member, Meeting meeting, boolean isHost) {
        this.member = member;
        this.meeting = meeting;
        this.isHost = isHost;
    }

    public boolean isHost() {
        return isHost;
    }
}
