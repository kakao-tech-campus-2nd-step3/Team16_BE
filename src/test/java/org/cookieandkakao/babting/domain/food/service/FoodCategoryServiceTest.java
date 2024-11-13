package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.entity.FoodCategory;
import org.cookieandkakao.babting.domain.food.repository.FoodCategoryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class FoodCategoryServiceTest {

    @Mock
    private FoodCategoryRepository foodCategoryRepository;

    @InjectMocks
    private FoodCategoryService foodCategoryService;

    public FoodCategoryServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void 음식카테고리_가져오기_테스트() {
        // given
        List<FoodCategory> categories = Arrays.asList(new FoodCategory("한식"), new FoodCategory("양식"));
        when(foodCategoryRepository.findAll()).thenReturn(categories);

        // when
        List<String> result = foodCategoryService.getFoodCategories();

        // then
        assertEquals(2, result.size());
        assertEquals("한식", result.get(0));
        assertEquals("양식", result.get(1));
    }
}
