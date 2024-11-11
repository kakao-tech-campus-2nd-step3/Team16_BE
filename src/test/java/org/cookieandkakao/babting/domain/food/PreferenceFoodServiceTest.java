package org.cookieandkakao.babting.domain.food;

import org.cookieandkakao.babting.domain.food.exception.FoodNotFoundException;
import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceCreateRequest;
import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.FoodCategory;
import org.cookieandkakao.babting.domain.food.entity.PreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.PreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.service.FoodRepositoryService;
import org.cookieandkakao.babting.domain.food.service.PreferenceFoodService;
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

public class PreferenceFoodServiceTest {

    @Mock
    private PreferenceFoodRepository preferenceFoodRepository;

    @Mock
    private FoodRepositoryService foodRepositoryService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private PreferenceFoodService preferenceFoodService;

    public PreferenceFoodServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddPreference() {
        FoodPreferenceCreateRequest request = new FoodPreferenceCreateRequest(1L);
        FoodCategory foodCategory = new FoodCategory("양식");
        Food food = new Food(1L, foodCategory, "피자");
        Member member = new Member(1L);
        PreferenceFood preferenceFood = new PreferenceFood(food, member);

        when(foodRepositoryService.findFoodById(1L)).thenReturn(food);
        when(memberService.findMember(1L)).thenReturn(member);

        when(preferenceFoodRepository.save(any(PreferenceFood.class))).thenReturn(preferenceFood);

        FoodPreferenceGetResponse result = preferenceFoodService.addPreference(request, 1L);

        assertNotNull(result);
        assertEquals("피자", result.name());
    }

    @Test
    public void testDeletePreference_선호음식없을때() {
        Food food = new Food(1L, new FoodCategory("양식"), "피자");
        Member member = new Member(1L);

        when(foodRepositoryService.findFoodById(1L)).thenReturn(food);
        when(memberService.findMember(1L)).thenReturn(member);

        when(preferenceFoodRepository.findByFoodAndMember(food, member)).thenReturn(Optional.empty());

        assertThrows(FoodNotFoundException.class, () -> {
            preferenceFoodService.deletePreference(1L, 1L);
        });
    }

    @Test
    public void testGetAllPreferencesByMember() {
        Member member = new Member(1L);
        FoodCategory foodCategory = new FoodCategory("양식");
        Food food = new Food(1L, foodCategory, "피자");
        PreferenceFood preferenceFood = new PreferenceFood(food, member);

        when(memberService.findMember(1L)).thenReturn(member);
        when(preferenceFoodRepository.findAllByMember(member)).thenReturn(Collections.singletonList(preferenceFood));

        List<FoodPreferenceGetResponse> preferences = preferenceFoodService.getAllPreferencesByMember(1L);

        assertNotNull(preferences);
        assertEquals(1, preferences.size());
        assertEquals("피자", preferences.get(0).name());
    }

    @Test
    public void testAddPreference_Success() {
        FoodPreferenceCreateRequest request = new FoodPreferenceCreateRequest(1L);
        FoodCategory foodCategory = new FoodCategory("양식");
        Food food = new Food(1L, foodCategory, "피자");
        Member member = new Member(1L);
        PreferenceFood preferenceFood = new PreferenceFood(food, member);

        when(foodRepositoryService.findFoodById(1L)).thenReturn(food);
        when(memberService.findMember(1L)).thenReturn(member);
        when(preferenceFoodRepository.save(any(PreferenceFood.class))).thenReturn(preferenceFood);

        FoodPreferenceGetResponse result = preferenceFoodService.addPreference(request, 1L);

        assertNotNull(result);
        assertEquals("피자", result.name());
    }
}
