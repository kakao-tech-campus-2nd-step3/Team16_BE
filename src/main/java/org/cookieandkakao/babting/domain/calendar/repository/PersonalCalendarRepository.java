package org.cookieandkakao.babting.domain.calendar.repository;

import org.cookieandkakao.babting.domain.calendar.entity.PersonalCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalCalendarRepository extends JpaRepository<PersonalCalendar, Long> {

}
