package org.cookieandkakao.babting.domain.food.controller;

import org.cookieandkakao.babting.domain.food.dto.ApiResponseDto;
import org.cookieandkakao.babting.domain.food.dto.PreferenceFoodCreateRequestDto;
import org.cookieandkakao.babting.domain.food.dto.PreferenceFoodGetResponseDto;
import org.cookieandkakao.babting.domain.food.service.PreferenceFoodService;
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
        List<PreferenceFoodGetResponseDto> preferences = preferenceFoodService.getAllPreferences();
        return ResponseEntity.ok().body(new ApiResponseDto(200, "선호 음식 조회 성공", preferences));
    }

    // 선호 음식 추가
    @PostMapping
    public ResponseEntity<?> addPreference(@RequestBody PreferenceFoodCreateRequestDto preferenceFoodCreateRequestDto) {
        PreferenceFoodGetResponseDto addedPreference = preferenceFoodService.addPreference(preferenceFoodCreateRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto(200, "선호 음식 추가 성공", addedPreference));
    }

    // 선호 음식 삭제
    @DeleteMapping
    public ResponseEntity<?> deletePreference(@RequestBody PreferenceFoodCreateRequestDto preferenceFoodCreateRequestDto) {
        preferenceFoodService.deletePreference(preferenceFoodCreateRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto(200, "선호 음식 삭제 성공", null));
    }
}
