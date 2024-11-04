package org.cookieandkakao.babting.domain.food.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "카테고리별 음식 조회", description = "음식의 카테고리(대분류)별 음식을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "카테고리별 음식 조회 성공")
    public ResponseEntity<SuccessBody<List<FoodGetResponse>>> getFoodsByCategory(
            @Parameter(description = "조회할 음식의 카테고리") @RequestParam String category) {
        List<FoodGetResponse> foods = foodService.getFoodsByCategory(category);
        return ApiResponseGenerator.success(HttpStatus.OK, "카테고리별 음식 조회 성공", foods);
    }
}
