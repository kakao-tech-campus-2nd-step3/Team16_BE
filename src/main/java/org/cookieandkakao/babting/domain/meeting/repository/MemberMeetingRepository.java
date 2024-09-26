package org.cookieandkakao.babting.domain.meeting.repository;

import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberMeetingRepository extends JpaRepository<MemberMeeting, Long> {
}
