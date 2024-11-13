package org.cookieandkakao.babting.domain.food.controller;

import org.cookieandkakao.babting.domain.food.dto.FoodGetResponse;
import org.cookieandkakao.babting.domain.food.service.FoodService;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.cookieandkakao.babting.domain.member.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FoodController.class)
public class FoodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FoodService foodService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void 카테고리로_음식_가져오기_테스트() throws Exception {
        // given
        String category = "한식";
        FoodGetResponse food = new FoodGetResponse(1L, category, "김치찌개");
        List<FoodGetResponse> foodList = Collections.singletonList(food);
        when(foodService.getFoodsByCategory(category)).thenReturn(foodList);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/foods").param("category", category));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("카테고리별 음식 조회 성공"))
                .andExpect(jsonPath("$.data[0].name").value("김치찌개"));
    }
}

