package org.cookieandkakao.babting.domain.food.controller;

import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.SuccessBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceCreateRequest;
import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.service.FoodPreferenceStrategy;
import org.cookieandkakao.babting.domain.food.service.NonPreferenceFoodService;
import org.cookieandkakao.babting.domain.food.service.PreferenceFoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FoodPreferenceController {

    private final Map<String, FoodPreferenceStrategy> strategies;

    public FoodPreferenceController(PreferenceFoodService preferenceFoodService,
                                    NonPreferenceFoodService nonPreferenceFoodService) {
        strategies = Map.of(
                "preferences", preferenceFoodService,
                "non-preferences", nonPreferenceFoodService
        );
    }

    // 선호/비선호 음식 추가
    @PostMapping("/{type}")
    public ResponseEntity<SuccessBody<FoodPreferenceGetResponse>> addFoodPreference(@PathVariable String type, @RequestBody FoodPreferenceCreateRequest request) {
        FoodPreferenceStrategy strategy = getStrategy(type);

        FoodPreferenceGetResponse response = strategy.addPreference(request);
        return ApiResponseGenerator.success(HttpStatus.OK, "음식 추가 성공", response);
    }


    // 선호/비선호 음식 조회
    @GetMapping("/{type}")
    public ResponseEntity<SuccessBody<List<FoodPreferenceGetResponse>>> getFoodPreferences(@PathVariable String type) {
        FoodPreferenceStrategy strategy = getStrategy(type);

        List<FoodPreferenceGetResponse> preferences = strategy.getAllPreferences();
        return ApiResponseGenerator.success(HttpStatus.OK, "음식 조회 성공", preferences);
    }

    // 선호/비선호 음식 삭제
    @DeleteMapping("/{type}")
    public ResponseEntity<SuccessBody<Void>> deleteFoodPreference(@PathVariable String type, @RequestBody FoodPreferenceCreateRequest request) {
        FoodPreferenceStrategy strategy = getStrategy(type);

        strategy.deletePreference(request.foodId());
        return ApiResponseGenerator.success(HttpStatus.OK, "음식 삭제 성공");
    }

    private FoodPreferenceStrategy getStrategy(String type) {
        FoodPreferenceStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Invalid preference type");
        }
        return strategy;
    }
}
