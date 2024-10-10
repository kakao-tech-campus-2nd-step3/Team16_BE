package org.cookieandkakao.babting.domain.food.repository;

import org.cookieandkakao.babting.domain.food.entity.MeetingNonPreferenceFood;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingNonPreferenceFoodRepository extends JpaRepository<MeetingNonPreferenceFood, Long> {
    List<MeetingNonPreferenceFood> findAllByMemberMeeting(MemberMeeting memberMeeting);

    void deleteAllByMemberMeeting(MemberMeeting memberMeeting);
}
