package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.FoodCategory;
import org.cookieandkakao.babting.domain.food.repository.MeetingNonPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.repository.MeetingPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.meeting.entity.Location;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.service.MeetingService;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MeetingFoodPreferenceUpdaterTest {

    @InjectMocks
    private MeetingFoodPreferenceUpdater updater;

    @Mock
    private MeetingPreferenceFoodRepository meetingPreferenceFoodRepository;
    @Mock
    private MeetingNonPreferenceFoodRepository meetingNonPreferenceFoodRepository;
    @Mock
    private FoodRepositoryService foodRepositoryService;
    @Mock
    private MemberService memberService;
    @Mock
    private MeetingService meetingService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 모임별선호비선호음식_수정하기_테스트() {
        // given
        Long memberId = 1L;
        Long meetingId = 1L;
        List<Long> preferences = List.of(1L, 2L);
        List<Long> nonPreferences = List.of(3L);

        Member member = new Member(memberId);

        Location location = new Location("카페", "광주 용봉동", 37.5, 127.0);
        String title = "저녁 모임";
        LocalDate startDate = LocalDate.of(2024, 11, 1);
        LocalDate endDate = LocalDate.of(2024, 11, 2);
        int durationTime = 2;
        LocalTime startTime = LocalTime.of(18, 0);
        LocalTime endTime = LocalTime.of(20, 0);

        Meeting meeting = new Meeting(location, title, startDate, endDate, durationTime, startTime, endTime);
        MemberMeeting memberMeeting = new MemberMeeting(member, meeting, false);

        // mocking
        when(memberService.findMember(memberId)).thenReturn(member);
        when(meetingService.findMeeting(meetingId)).thenReturn(meeting);
        when(meetingService.findMemberMeeting(member, meeting)).thenReturn(memberMeeting);

        FoodCategory foodCategory1 = new FoodCategory("한식");
        FoodCategory foodCategory2 = new FoodCategory("양식");
        FoodCategory foodCategory3 = new FoodCategory("일식");

        Food food1 = new Food(1L, foodCategory1, "김치찌개");
        Food food2 = new Food(2L, foodCategory2, "스파게티");
        Food food3 = new Food(3L, foodCategory3, "우동");

        when(foodRepositoryService.findFoodsByIds(any(Set.class)))
                .thenReturn(List.of(food1, food2, food3));

        // when
        updater.updatePreferences(meetingId, memberId, preferences, nonPreferences);

        // then
        verify(meetingPreferenceFoodRepository, times(1)).deleteAllByMemberMeeting(memberMeeting);
        verify(meetingNonPreferenceFoodRepository, times(1)).deleteAllByMemberMeeting(memberMeeting);

        verify(meetingPreferenceFoodRepository, times(1)).saveAll(any(List.class));
        verify(meetingNonPreferenceFoodRepository, times(1)).saveAll(any(List.class));
    }

}
