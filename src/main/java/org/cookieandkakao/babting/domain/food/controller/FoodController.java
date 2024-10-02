package org.cookieandkakao.babting.domain.food.controller;

import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.SuccessBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.food.dto.FoodGetResponse;
import org.cookieandkakao.babting.domain.food.service.FoodService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<SuccessBody<List<FoodGetResponse>>> getFoodsByCategory(@RequestParam String category) {
        List<FoodGetResponse> foods = foodService.getFoodsByCategory(category);
        return ApiResponseGenerator.success(HttpStatus.OK, "카테고리별 음식 조회 성공", foods);
    }
}
