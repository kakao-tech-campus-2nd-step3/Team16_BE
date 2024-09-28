package org.cookieandkakao.babting.domain.food.service;

import org.cookieandkakao.babting.domain.food.repository.FoodCategoryRepository;
import org.cookieandkakao.babting.domain.food.entity.FoodCategory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodCategoryService {

    private final FoodCategoryRepository foodCategoryRepository;

    public FoodCategoryService(FoodCategoryRepository foodCategoryRepository) {
        this.foodCategoryRepository = foodCategoryRepository;
    }

    public List<String> getFoodCategories() {
        return foodCategoryRepository.findAll()
                .stream()
                .map(FoodCategory::getName)
                .collect(Collectors.toList());
    }
}
