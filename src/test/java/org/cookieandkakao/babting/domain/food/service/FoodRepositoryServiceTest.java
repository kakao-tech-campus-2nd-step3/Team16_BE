package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.exception.FoodNotFoundException;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.FoodCategory;
import org.cookieandkakao.babting.domain.food.repository.FoodRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class FoodRepositoryServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private FoodRepositoryService foodRepositoryService;

    public FoodRepositoryServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void FindFoodById_테스트() {
        // given
        FoodCategory foodCategory = new FoodCategory("양식");
        Food food = new Food(1L, foodCategory, "피자");

        // mocking
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        // when
        Food result = foodRepositoryService.findFoodById(1L);

        // then
        assertNotNull(result);
        assertEquals("피자", result.getName());
    }

    @Test
    public void Food가_존재하지_않을때_FindFoodById_테스트() {
        // mocking
        when(foodRepository.findById(2L)).thenReturn(Optional.empty());

        // then
        assertThrows(FoodNotFoundException.class, () -> foodRepositoryService.findFoodById(2L));
    }
}
