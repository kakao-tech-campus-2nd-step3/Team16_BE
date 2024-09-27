package org.cookieandkakao.babting.domain.food.controller;

import org.cookieandkakao.babting.domain.food.dto.FoodResponseDto;
import org.cookieandkakao.babting.domain.food.service.FoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/foods")
    public ResponseEntity<Map<String, Object>> getFoodsByCategory(@RequestParam String category) {
        List<FoodResponseDto> foods = foodService.getFoodsByCategory(category);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "카테고리별 음식 조회 성공");
        response.put("data", foods);

        return ResponseEntity.ok(response);
    }
}

