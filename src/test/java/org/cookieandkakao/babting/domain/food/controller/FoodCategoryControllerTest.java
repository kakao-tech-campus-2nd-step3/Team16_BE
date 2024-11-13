package org.cookieandkakao.babting.domain.food.controller;

import org.cookieandkakao.babting.domain.food.service.FoodCategoryService;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.cookieandkakao.babting.domain.member.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FoodCategoryController.class)
public class FoodCategoryControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private FoodCategoryService foodCategoryService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new FoodCategoryController(foodCategoryService)).build();
    }

    @Test
    public void 음식카테고리_조회_테스트() throws Exception {
        // given
        List<String> categories = Arrays.asList("양식", "한식", "일식");
        when(foodCategoryService.getFoodCategories()).thenReturn(categories);

        // when
        mockMvc.perform(get("/api/food-categories").contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("음식 카테고리 목록 조회 성공"))
                .andExpect(jsonPath("$.data[0]").value("양식"))
                .andExpect(jsonPath("$.data[1]").value("한식"))
                .andExpect(jsonPath("$.data[2]").value("일식"));
    }
}
