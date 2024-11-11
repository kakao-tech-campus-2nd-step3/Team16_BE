package org.cookieandkakao.babting.domain.food;

import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.FoodCategory;
import org.cookieandkakao.babting.domain.food.entity.MeetingPreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.MeetingPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.service.MeetingPreferenceFoodService;
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
    public void testGetAllPreferencesByMeeting() {
        Long meetingId = 1L;
        Long memberId = 1L;
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

        FoodCategory foodCategory = new FoodCategory("양식");
        Food food = new Food(1L, foodCategory, "피자");
        MeetingPreferenceFood meetingPreferenceFood = new MeetingPreferenceFood(food, memberMeeting);

        when(memberService.findMember(memberId)).thenReturn(member);
        when(meetingService.findMeeting(meetingId)).thenReturn(meeting);
        when(meetingService.findMemberMeeting(member, meeting)).thenReturn(memberMeeting);
        when(meetingPreferenceFoodRepository.findAllByMemberMeeting(memberMeeting))
                .thenReturn(Collections.singletonList(meetingPreferenceFood));

        List<FoodPreferenceGetResponse> preferences = meetingPreferenceFoodService.getAllPreferencesByMeeting(meetingId, memberId);

        assertNotNull(preferences);
        assertEquals(1, preferences.size());
        assertEquals("피자", preferences.get(0).name());
        assertEquals("양식", preferences.get(0).category());
    }
}
