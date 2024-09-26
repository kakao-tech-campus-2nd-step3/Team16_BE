package org.cookieandkakao.babting.domain.meeting.repository;

import org.cookieandkakao.babting.domain.meeting.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}
