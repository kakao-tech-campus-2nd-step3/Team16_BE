package org.cookieandkakao.babting.domain.calendar.repository;

import org.cookieandkakao.babting.domain.calendar.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

}
