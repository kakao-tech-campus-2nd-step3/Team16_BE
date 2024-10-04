package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.dto.FoodGetResponse;
import org.cookieandkakao.babting.domain.food.entity.FoodCategory;
import org.cookieandkakao.babting.domain.food.repository.FoodCategoryRepository;
import org.cookieandkakao.babting.domain.food.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodService {

    private final FoodRepository foodRepository;
    private final FoodCategoryRepository foodCategoryRepository;

    public FoodService(FoodRepository foodRepository, FoodCategoryRepository foodCategoryRepository) {
        this.foodRepository = foodRepository;
        this.foodCategoryRepository = foodCategoryRepository;
    }

    public List<FoodGetResponse> getFoodsByCategory(String categoryName) {
        FoodCategory category = foodCategoryRepository.findByName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

        return foodRepository.findByFoodCategory(category)
                .stream()
                .map(food -> new FoodGetResponse(food.getFoodId(), category.getName(), food.getName()))
                .collect(Collectors.toList());
    }
}
