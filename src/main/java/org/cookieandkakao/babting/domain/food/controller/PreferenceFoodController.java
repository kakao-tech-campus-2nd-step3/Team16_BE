package org.cookieandkakao.babting.domain.food.controller;

import org.cookieandkakao.babting.domain.food.dto.ApiResponse;
import org.cookieandkakao.babting.domain.food.dto.PreferenceFoodDto;
import org.cookieandkakao.babting.domain.food.dto.PreferenceFoodResponseDto;
import org.cookieandkakao.babting.domain.food.service.PreferenceFoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceFoodController {

    @Autowired
    private PreferenceFoodService preferenceFoodService;

    // 선호 음식 전체 조회
    @GetMapping
    public ResponseEntity<?> getPreferences() {
        List<PreferenceFoodResponseDto> preferences = preferenceFoodService.getAllPreferences();
        return ResponseEntity.ok().body(new ApiResponse(200, "선호 음식 조회 성공", preferences));
    }

    // 선호 음식 추가
    @PostMapping
    public ResponseEntity<?> addPreference(@RequestBody PreferenceFoodDto preferenceFoodDto) {
        PreferenceFoodResponseDto addedPreference = preferenceFoodService.addPreference(preferenceFoodDto);
        return ResponseEntity.ok().body(new ApiResponse(200, "선호 음식 추가 성공", addedPreference));
    }

    // 선호 음식 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePreference(@PathVariable Long id) {
        preferenceFoodService.deletePreference(id);
        return ResponseEntity.ok().body(new ApiResponse(200, "선호 음식 삭제 성공", null));
    }
}
