package org.cookieandkakao.babting.domain.food.controller;

import org.cookieandkakao.babting.domain.food.dto.ApiResponseDto;
import org.cookieandkakao.babting.domain.food.dto.FoodResponseDto;
import org.cookieandkakao.babting.domain.food.service.FoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/foods")
    public ResponseEntity<ApiResponseDto> getFoodsByCategory(@RequestParam String category) {
        List<FoodResponseDto> foods = foodService.getFoodsByCategory(category);

        ApiResponseDto response = new ApiResponseDto(200, "카테고리별 음식 조회 성공", foods);

        return ResponseEntity.ok(response);
    }
}
