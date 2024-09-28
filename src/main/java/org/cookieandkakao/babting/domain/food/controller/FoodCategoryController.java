package org.cookieandkakao.babting.domain.food.controller;

import org.cookieandkakao.babting.domain.food.dto.ApiResponseDto;
import org.cookieandkakao.babting.domain.food.service.FoodCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FoodCategoryController {

    private final FoodCategoryService foodCategoryService;

    public FoodCategoryController(FoodCategoryService foodCategoryService) {
        this.foodCategoryService = foodCategoryService;
    }

    @GetMapping("/food-categories")
    public ResponseEntity<ApiResponseDto> getFoodCategories() {
        List<String> categories = foodCategoryService.getFoodCategories();

        ApiResponseDto response = new ApiResponseDto(200, "음식 카테고리 목록 조회 성공", categories);

        return ResponseEntity.ok(response);
    }
}
