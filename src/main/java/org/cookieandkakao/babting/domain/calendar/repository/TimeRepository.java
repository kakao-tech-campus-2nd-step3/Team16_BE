package org.cookieandkakao.babting.domain.calendar.repository;

import org.cookieandkakao.babting.domain.calendar.entity.Time;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeRepository extends JpaRepository<Time, Long> {

}
