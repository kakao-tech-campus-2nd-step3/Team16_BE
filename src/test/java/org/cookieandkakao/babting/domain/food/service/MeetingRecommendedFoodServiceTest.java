package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.FoodCategory;
import org.cookieandkakao.babting.domain.food.entity.MeetingNonPreferenceFood;
import org.cookieandkakao.babting.domain.food.entity.MeetingPreferenceFood;
import org.cookieandkakao.babting.domain.food.entity.NonPreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.MeetingNonPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.repository.MeetingPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.repository.NonPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.meeting.entity.Location;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.repository.MemberMeetingRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MeetingRecommendedFoodServiceTest {

    @InjectMocks
    private MeetingRecommendedFoodService recommendedFoodService;

    @Mock
    private MemberMeetingRepository memberMeetingRepository;
    @Mock
    private MeetingPreferenceFoodRepository meetingPreferenceFoodRepository;
    @Mock
    private MeetingNonPreferenceFoodRepository meetingNonPreferenceFoodRepository;
    @Mock
    private NonPreferenceFoodRepository nonPreferenceFoodRepository;
    @Mock
    private FoodRepositoryService foodRepositoryService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 추천음식_계산하기_테스트() {
        // given
        Long meetingId = 1L;
        Long memberId = 1L;
        Member member = new Member(memberId);

        Location location = new Location("카페", "광주 용봉동", 37.5, 127.0);
        Meeting meeting = new Meeting(location, "저녁 모임", LocalDate.of(2024, 11, 1), LocalDate.of(2024, 11, 2), 2, LocalTime.of(18, 0), LocalTime.of(20, 0));
        MemberMeeting memberMeeting = new MemberMeeting(member, meeting, false);

        when(memberMeetingRepository.findMemberMeetingsByMeetingId(meetingId)).thenReturn(List.of(memberMeeting));

        FoodCategory foodCategory1 = new FoodCategory("한식");
        FoodCategory foodCategory2 = new FoodCategory("양식");
        Food food1 = new Food(1L, foodCategory1, "김치찌개");
        Food food2 = new Food(2L, foodCategory2, "스파게티");
        MeetingPreferenceFood preferenceFood = new MeetingPreferenceFood(food1, memberMeeting);
        MeetingNonPreferenceFood nonPreferenceFood = new MeetingNonPreferenceFood(food2, memberMeeting);
        NonPreferenceFood personalNonPreferenceFood = new NonPreferenceFood(food2, member);

        when(meetingPreferenceFoodRepository.findAllByMemberMeeting(memberMeeting)).thenReturn(List.of(preferenceFood));
        when(meetingNonPreferenceFoodRepository.findAllByMemberMeeting(memberMeeting)).thenReturn(List.of(nonPreferenceFood));
        when(nonPreferenceFoodRepository.findAllByMember(any())).thenReturn(List.of(personalNonPreferenceFood));
        when(foodRepositoryService.findFoodsByIds(Set.of(1L))).thenReturn(List.of(food1));

        // when
        List<FoodPreferenceGetResponse> responses = recommendedFoodService.getRecommendedFoodDetailsForMeeting(meetingId);

        // then
        assertEquals(1, responses.size());
        assertEquals("김치찌개", responses.get(0).name());
        assertEquals("한식", responses.get(0).category());
        assertEquals(1L, responses.get(0).foodId());
    }
}
