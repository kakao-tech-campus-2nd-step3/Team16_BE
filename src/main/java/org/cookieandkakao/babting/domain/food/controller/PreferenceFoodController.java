package org.cookieandkakao.babting.domain.food.controller;

import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.food.dto.PreferenceFoodCreateRequest;
import org.cookieandkakao.babting.domain.food.dto.PreferenceFoodGetResponse;
import org.cookieandkakao.babting.domain.food.service.PreferenceFoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceFoodController {

    private final PreferenceFoodService preferenceFoodService;

    public PreferenceFoodController(final PreferenceFoodService preferenceFoodService) {
        this.preferenceFoodService = preferenceFoodService;
    }

    // 선호 음식 전체 조회
    @GetMapping
    public ResponseEntity<?> getPreferences() {
        List<PreferenceFoodGetResponse> preferences = preferenceFoodService.getAllPreferences();
        return ApiResponseGenerator.success(HttpStatus.OK, "선호 음식 조회 성공", preferences);
    }

    // 선호 음식 추가
    @PostMapping
    public ResponseEntity<?> addPreference(@RequestBody PreferenceFoodCreateRequest preferenceFoodCreateRequest) {
        PreferenceFoodGetResponse addedPreference = preferenceFoodService.addPreference(preferenceFoodCreateRequest);
        return ApiResponseGenerator.success(HttpStatus.OK, "선호 음식 추가 성공", addedPreference);
    }

    // 선호 음식 삭제
    @DeleteMapping
    public ResponseEntity<?> deletePreference(@RequestBody PreferenceFoodCreateRequest preferenceFoodCreateRequest) {
        preferenceFoodService.deletePreference(preferenceFoodCreateRequest);
        return ApiResponseGenerator.success(HttpStatus.OK, "선호 음식 삭제 성공");
    }
}
