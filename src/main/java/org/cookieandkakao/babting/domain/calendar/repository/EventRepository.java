package org.cookieandkakao.babting.domain.calendar.repository;

import org.cookieandkakao.babting.domain.calendar.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

}
