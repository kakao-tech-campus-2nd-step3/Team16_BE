package org.cookieandkakao.babting.domain.meeting.repository;

import java.util.Optional;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberMeetingRepository extends JpaRepository<MemberMeeting, Long> {
    Optional<MemberMeeting> findMemberMeetingByMemberAndMeeting(Member member, Meeting meeting);
    void deleteAllByMeeting(Meeting meeting);
}
