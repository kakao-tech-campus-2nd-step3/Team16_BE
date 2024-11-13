package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.exception.FoodNotFoundException;
import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceCreateRequest;
import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.FoodCategory;
import org.cookieandkakao.babting.domain.food.entity.NonPreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.NonPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class NonPreferenceFoodServiceTest {

    @Mock
    private NonPreferenceFoodRepository nonPreferenceFoodRepository;

    @Mock
    private FoodRepositoryService foodRepositoryService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private NonPreferenceFoodService nonPreferenceFoodService;

    public NonPreferenceFoodServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void 비선호음식_추가하기_테스트() {
        // given
        FoodPreferenceCreateRequest request = new FoodPreferenceCreateRequest(1L);
        FoodCategory foodCategory = new FoodCategory("양식");
        Food food = new Food(1L, foodCategory, "피자");
        Member member = new Member(1L);
        NonPreferenceFood nonPreferenceFood = new NonPreferenceFood(food, member);

        when(foodRepositoryService.findFoodById(1L)).thenReturn(food);
        when(memberService.findMember(1L)).thenReturn(member);
        when(nonPreferenceFoodRepository.save(any(NonPreferenceFood.class))).thenReturn(nonPreferenceFood);

        // when
        FoodPreferenceGetResponse result = nonPreferenceFoodService.addPreference(request, 1L);

        // then
        assertNotNull(result);
        assertEquals("피자", result.name());
    }

    @Test
    public void 비선호음식없을때_비선호음식_삭제하기_테스트() {
        // given
        Food food = new Food(1L, new FoodCategory("양식"), "피자");
        Member member = new Member(1L);

        when(foodRepositoryService.findFoodById(1L)).thenReturn(food);
        when(memberService.findMember(1L)).thenReturn(member);
        when(nonPreferenceFoodRepository.findByFoodAndMember(food, member)).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(FoodNotFoundException.class, () -> {
            nonPreferenceFoodService.deletePreference(1L, 1L);
        });
    }

    @Test
    public void Member로_모든_비선호음식_가져오기_테스트() {
        // given
        Member member = new Member(1L);
        FoodCategory foodCategory = new FoodCategory("양식");
        Food food = new Food(1L, foodCategory, "피자");
        NonPreferenceFood nonPreferenceFood = new NonPreferenceFood(food, member);

        when(memberService.findMember(1L)).thenReturn(member);
        when(nonPreferenceFoodRepository.findAllByMember(member)).thenReturn(Collections.singletonList(nonPreferenceFood));

        // when
        List<FoodPreferenceGetResponse> nonPreferences = nonPreferenceFoodService.getAllPreferencesByMember(1L);

        // then
        assertNotNull(nonPreferences);
        assertEquals(1, nonPreferences.size());
        assertEquals("피자", nonPreferences.get(0).name());
    }
}
