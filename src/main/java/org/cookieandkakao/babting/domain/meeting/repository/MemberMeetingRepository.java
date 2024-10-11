package org.cookieandkakao.babting.domain.meeting.repository;

import java.util.List;
import java.util.Optional;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberMeetingRepository extends JpaRepository<MemberMeeting, Long> {
    Optional<MemberMeeting> findByMemberAndMeeting(Member member, Meeting meeting);
    @Query("SELECT mm.meeting FROM MemberMeeting mm WHERE mm.member = :member")
    List<Meeting> findMeetingsByMember(@Param("member") Member member);
    boolean existsByMemberAndMeeting(Member member, Meeting meeting);
    void deleteAllByMeeting(Meeting meeting);
}
