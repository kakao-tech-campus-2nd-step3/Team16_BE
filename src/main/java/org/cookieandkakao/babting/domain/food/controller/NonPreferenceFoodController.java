package org.cookieandkakao.babting.domain.food.controller;

import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.food.dto.ApiResponseDto;
import org.cookieandkakao.babting.domain.food.dto.NonPreferenceFoodCreateRequestDto;
import org.cookieandkakao.babting.domain.food.dto.NonPreferenceFoodGetResponseDto;
import org.cookieandkakao.babting.domain.food.service.NonPreferenceFoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/non-preferences")
public class NonPreferenceFoodController {

    private final NonPreferenceFoodService nonPreferenceFoodService;

    public NonPreferenceFoodController(NonPreferenceFoodService nonPreferenceFoodService) {
        this.nonPreferenceFoodService = nonPreferenceFoodService;
    }

    // 선호 음식 전체 조회
    @GetMapping
    public ResponseEntity<?> getPreferences() {
        List<NonPreferenceFoodGetResponseDto> nonPreferences = nonPreferenceFoodService.getAllNonPreferences();
        return ApiResponseGenerator.success(HttpStatus.OK, "선호 음식 조회 성공", nonPreferences);
    }

    // 선호 음식 추가
    @PostMapping
    public ResponseEntity<?> addPreference(@RequestBody NonPreferenceFoodCreateRequestDto nonPreferenceFoodCreateRequestDto) {
        NonPreferenceFoodGetResponseDto addedNonPreference = nonPreferenceFoodService.addNonPreference(nonPreferenceFoodCreateRequestDto);
        return ApiResponseGenerator.success(HttpStatus.OK, "비선호 음식 추가 성공", addedNonPreference);
    }

    // 선호 음식 삭제
    @DeleteMapping
    public ResponseEntity<?> deletePreference(@RequestBody NonPreferenceFoodCreateRequestDto nonPreferenceFoodCreateRequestDto) {
        nonPreferenceFoodService.deleteNonPreference(nonPreferenceFoodCreateRequestDto);
        return ApiResponseGenerator.success(HttpStatus.OK, "비선호 음식 삭제 성공");
    }
}
