package org.cookieandkakao.babting.domain.meeting.repository;

import org.cookieandkakao.babting.domain.meeting.entity.MeetingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingEventRepository extends JpaRepository<MeetingEvent, Long> {
}
