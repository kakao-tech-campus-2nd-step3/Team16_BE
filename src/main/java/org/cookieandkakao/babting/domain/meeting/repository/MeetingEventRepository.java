package org.cookieandkakao.babting.domain.meeting.repository;

import java.util.List;
import java.util.Optional;
import org.cookieandkakao.babting.domain.meeting.entity.MeetingEvent;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingEventRepository extends JpaRepository<MeetingEvent, Long> {
    List<MeetingEvent> findByMemberMeeting(MemberMeeting memberMeeting);
}
