package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.FoodCategory;
import org.cookieandkakao.babting.domain.food.entity.MeetingPreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.MeetingPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.meeting.entity.Location;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.service.MeetingService;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class MeetingPreferenceFoodServiceTest {

    @Mock
    private MeetingPreferenceFoodRepository meetingPreferenceFoodRepository;

    @Mock
    private MeetingService meetingService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MeetingPreferenceFoodService meetingPreferenceFoodService;

    public MeetingPreferenceFoodServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void Meeting으로_모임별선호음식_가져오기_테스트() {
        // given
        Long meetingId = 1L;
        Long memberId = 1L;
        Member member = new Member(memberId);

        Location location = new Location("카페", "광주 용봉동", 37.5, 127.0);
        Meeting meeting = new Meeting(location, "저녁 모임", LocalDate.of(2024, 11, 1), LocalDate.of(2024, 11, 2), 2, LocalTime.of(18, 0), LocalTime.of(20, 0));
        MemberMeeting memberMeeting = new MemberMeeting(member, meeting, false);
        FoodCategory foodCategory = new FoodCategory("양식");
        Food food = new Food(1L, foodCategory, "피자");
        MeetingPreferenceFood meetingPreferenceFood = new MeetingPreferenceFood(food, memberMeeting);

        when(memberService.findMember(memberId)).thenReturn(member);
        when(meetingService.findMeeting(meetingId)).thenReturn(meeting);
        when(meetingService.findMemberMeeting(member, meeting)).thenReturn(memberMeeting);
        when(meetingPreferenceFoodRepository.findAllByMemberMeeting(memberMeeting))
                .thenReturn(Collections.singletonList(meetingPreferenceFood));

        // when
        List<FoodPreferenceGetResponse> preferences = meetingPreferenceFoodService.getAllPreferencesByMeeting(meetingId, memberId);

        // then
        assertNotNull(preferences);
        assertEquals(1, preferences.size());
        assertEquals("피자", preferences.get(0).name());
        assertEquals("양식", preferences.get(0).category());
    }
}
