package org.cookieandkakao.babting.domain.food.repository;

import org.cookieandkakao.babting.domain.food.entity.MeetingPreferenceFood;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingPreferenceFoodRepository extends JpaRepository<MeetingPreferenceFood, Long> {
    List<MeetingPreferenceFood> findAllByMemberMeeting(MemberMeeting memberMeeting);

    void deleteAllByMemberMeeting(MemberMeeting memberMeeting);
}
