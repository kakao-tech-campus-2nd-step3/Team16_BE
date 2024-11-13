package org.cookieandkakao.babting.domain.food.controller;

import org.cookieandkakao.babting.common.resolver.LoginMemberIdArgumentResolver;
import org.cookieandkakao.babting.domain.food.service.NonPreferenceFoodService;
import org.cookieandkakao.babting.domain.food.service.PreferenceFoodService;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FoodPreferenceController.class)
public class FoodPreferenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PreferenceFoodService preferenceFoodService;

    @MockBean
    private NonPreferenceFoodService nonPreferenceFoodService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private LoginMemberIdArgumentResolver loginMemberIdArgumentResolver;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void 비호음식_가져오기_테스트() throws Exception {
        // given
        when(preferenceFoodService.getAllPreferencesByMember(anyLong())).thenReturn(Collections.emptyList());

        // when
        mockMvc.perform(get("/api/preferences").header("memberId", 1L))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("조회된 음식이 없습니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void 비선호음식_가져오기_테스트() throws Exception {
        // given
        when(nonPreferenceFoodService.getAllPreferencesByMember(anyLong())).thenReturn(Collections.emptyList());

        // when
        mockMvc.perform(get("/api/non-preferences").header("memberId", 1L))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("조회된 음식이 없습니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void 선호음식_삭제하기_테스트() throws Exception {
        // given
        doNothing().when(preferenceFoodService).deletePreference(anyLong(), anyLong());

        // when
        mockMvc.perform(delete("/api/preferences")
                        .header("memberId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"foodId\":1}"))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("음식 삭제 성공"));
    }

    @Test
    public void 비선호음식_삭제하기_테스트() throws Exception {
        // given
        doNothing().when(nonPreferenceFoodService).deletePreference(anyLong(), anyLong());

        // when
        mockMvc.perform(delete("/api/non-preferences")
                        .header("memberId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"foodId\":2}"))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("음식 삭제 성공"));
    }
}
