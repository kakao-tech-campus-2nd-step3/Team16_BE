package org.cookieandkakao.babting.domain.food.controller;

import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.service.MeetingFoodPreferenceUpdater;
import org.cookieandkakao.babting.domain.food.service.MeetingNonPreferenceFoodService;
import org.cookieandkakao.babting.domain.food.service.MeetingPreferenceFoodService;
import org.cookieandkakao.babting.domain.food.service.MeetingRecommendedFoodService;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.cookieandkakao.babting.domain.member.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberMeetingFoodPreferenceController.class)
public class MemberMeetingFoodPreferenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private MeetingPreferenceFoodService meetingPreferenceFoodService;

    @MockBean
    private MeetingNonPreferenceFoodService meetingNonPreferenceFoodService;

    @MockBean
    private MeetingFoodPreferenceUpdater meetingFoodPreferenceUpdater;

    @MockBean
    private MeetingRecommendedFoodService meetingRecommendedFoodService;

    @MockBean
    private MemberRepository memberRepository;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void 모임별추천음식_가져오기() throws Exception {
        // given
        FoodPreferenceGetResponse response = new FoodPreferenceGetResponse(1L, "양식", "피자");
        when(meetingRecommendedFoodService.getRecommendedFoodDetailsForMeeting(anyLong()))
                .thenReturn(Collections.singletonList(response));

        // when
        mockMvc.perform(get("/api/meeting/1/recommend"))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("모임 추천 음식 조회 성공"))
                .andExpect(jsonPath("$.data[0].foodId").value(1L))
                .andExpect(jsonPath("$.data[0].category").value("양식"))
                .andExpect(jsonPath("$.data[0].name").value("피자"));
    }
}
