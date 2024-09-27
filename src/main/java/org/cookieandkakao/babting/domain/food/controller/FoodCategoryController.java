package org.cookieandkakao.babting.domain.food.controller;

import org.cookieandkakao.babting.domain.food.service.FoodCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FoodCategoryController {

    private final FoodCategoryService foodCategoryService;

    public FoodCategoryController(FoodCategoryService foodCategoryService) {
        this.foodCategoryService = foodCategoryService;
    }

    @GetMapping("/food-categories")
    public ResponseEntity<Map<String, Object>> getFoodCategories() {
        List<String> categories = foodCategoryService.getFoodCategories();

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "음식 카테고리 목록 조회 성공");
        response.put("data", categories);

        return ResponseEntity.ok(response);
    }
}
