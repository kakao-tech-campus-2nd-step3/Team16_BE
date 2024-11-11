package org.cookieandkakao.babting.domain.food;

import org.cookieandkakao.babting.domain.food.exception.FoodNotFoundException;
import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceCreateRequest;
import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.FoodCategory;
import org.cookieandkakao.babting.domain.food.entity.NonPreferenceFood;
import org.cookieandkakao.babting.domain.food.repository.NonPreferenceFoodRepository;
import org.cookieandkakao.babting.domain.food.service.FoodRepositoryService;
import org.cookieandkakao.babting.domain.food.service.NonPreferenceFoodService;
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
    public void testAddNonPreference() {
        FoodPreferenceCreateRequest request = new FoodPreferenceCreateRequest(1L);
        FoodCategory foodCategory = new FoodCategory("양식");
        Food food = new Food(1L, foodCategory, "피자");
        Member member = new Member(1L);
        NonPreferenceFood nonPreferenceFood = new NonPreferenceFood(food, member);

        when(foodRepositoryService.findFoodById(1L)).thenReturn(food);
        when(memberService.findMember(1L)).thenReturn(member);
        when(nonPreferenceFoodRepository.save(any(NonPreferenceFood.class))).thenReturn(nonPreferenceFood);

        FoodPreferenceGetResponse result = nonPreferenceFoodService.addPreference(request, 1L);

        assertNotNull(result);
        assertEquals("피자", result.name());
    }

    @Test
    public void testDeleteNonPreference_비선호음식없을때() {
        Food food = new Food(1L, new FoodCategory("양식"), "피자");
        Member member = new Member(1L);

        when(foodRepositoryService.findFoodById(1L)).thenReturn(food);
        when(memberService.findMember(1L)).thenReturn(member);

        when(nonPreferenceFoodRepository.findByFoodAndMember(food, member)).thenReturn(Optional.empty());

        assertThrows(FoodNotFoundException.class, () -> {
            nonPreferenceFoodService.deletePreference(1L, 1L);
        });
    }

    @Test
    public void testGetAllNonPreferencesByMember() {
        Member member = new Member(1L);
        FoodCategory foodCategory = new FoodCategory("양식");
        Food food = new Food(1L, foodCategory, "피자");
        NonPreferenceFood nonPreferenceFood = new NonPreferenceFood(food, member);

        when(memberService.findMember(1L)).thenReturn(member);
        when(nonPreferenceFoodRepository.findAllByMember(member)).thenReturn(Collections.singletonList(nonPreferenceFood));

        List<FoodPreferenceGetResponse> nonPreferences = nonPreferenceFoodService.getAllPreferencesByMember(1L);

        assertNotNull(nonPreferences);
        assertEquals(1, nonPreferences.size());
        assertEquals("피자", nonPreferences.get(0).name());
    }

    @Test
    public void testAddNonPreference_Success() {
        FoodPreferenceCreateRequest request = new FoodPreferenceCreateRequest(1L);
        FoodCategory foodCategory = new FoodCategory("양식");
        Food food = new Food(1L, foodCategory, "피자");
        Member member = new Member(1L);
        NonPreferenceFood nonPreferenceFood = new NonPreferenceFood(food, member);

        when(foodRepositoryService.findFoodById(1L)).thenReturn(food);
        when(memberService.findMember(1L)).thenReturn(member);
        when(nonPreferenceFoodRepository.save(any(NonPreferenceFood.class))).thenReturn(nonPreferenceFood);

        FoodPreferenceGetResponse result = nonPreferenceFoodService.addPreference(request, 1L);

        assertNotNull(result);
        assertEquals("피자", result.name());
    }
}
