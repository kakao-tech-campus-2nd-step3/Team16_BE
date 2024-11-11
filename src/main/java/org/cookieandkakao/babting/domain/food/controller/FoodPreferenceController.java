package org.cookieandkakao.babting.domain.food.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cookieandkakao.babting.common.annotaion.LoginMemberId;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.SuccessBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.food.exception.InvalidFoodPreferenceTypeException;
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

@Tag(name = "개인 선호/비선호 음식", description = "개인 선호/비선호 음식 관련 api입니다.")
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

    // 선호/비선호 음식 조회
    @GetMapping("/{type}")
    @Operation(summary = "선호/비선호 음식 조회", description = "내 선호/비선호 음식을 가져옵니다.")
    @ApiResponse(responseCode = "200", description = "선호/비선호 음식 조회 성공")
    public ResponseEntity<SuccessBody<List<FoodPreferenceGetResponse>>> getFoodPreferences(
            @PathVariable String type,
            @LoginMemberId Long memberId
    ) {
        FoodPreferenceStrategy strategy = getStrategy(type);
        List<FoodPreferenceGetResponse> preferences = strategy.getAllPreferencesByMember(memberId);

        if (preferences.isEmpty()) {
            return ApiResponseGenerator.success(HttpStatus.OK, "조회된 음식이 없습니다", preferences);
        }

        return ApiResponseGenerator.success(HttpStatus.OK, "음식 조회 성공", preferences);
    }

    // 선호/비선호 음식 추가
    @PostMapping("/{type}")
    @Operation(summary = "선호/비선호 음식 추가", description = "내 선호/비선호 음식을 추가합니다.")
    @ApiResponse(responseCode = "200", description = "선호/비선호 음식 추가 성공")
    public ResponseEntity<SuccessBody<FoodPreferenceGetResponse>> addFoodPreference(
            @PathVariable String type,
            @RequestBody FoodPreferenceCreateRequest request,
            @LoginMemberId Long memberId
    ) {
        FoodPreferenceStrategy strategy = getStrategy(type);

        FoodPreferenceGetResponse response = strategy.addPreference(request, memberId);
        return ApiResponseGenerator.success(HttpStatus.OK, "음식 추가 성공", response);
    }

    // 선호/비선호 음식 삭제
    @DeleteMapping("/{type}")
    @Operation(summary = "선호/비선호 음식 삭제", description = "내 선호/비선호 음식을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "선호/비선호 음식 삭제 성공")
    public ResponseEntity<SuccessBody<Void>> deleteFoodPreference(
            @PathVariable String type,
            @RequestBody FoodPreferenceCreateRequest request,
            @LoginMemberId Long memberId
    ) {
        FoodPreferenceStrategy strategy = getStrategy(type);

        strategy.deletePreference(request.foodId(), memberId);
        return ApiResponseGenerator.success(HttpStatus.OK, "음식 삭제 성공");
    }

    private FoodPreferenceStrategy getStrategy(String type) {
        FoodPreferenceStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new InvalidFoodPreferenceTypeException("잘못된 선호 타입입니다");
        }
        return strategy;
    }
}
