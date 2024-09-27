package org.cookieandkakao.babting.domain.food.controller;

import org.cookieandkakao.babting.domain.food.dto.ApiResponseDto;
import org.cookieandkakao.babting.domain.food.dto.NonPreferenceFoodDto;
import org.cookieandkakao.babting.domain.food.dto.NonPreferenceFoodResponseDto;
import org.cookieandkakao.babting.domain.food.service.NonPreferenceFoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/non-preferences")
public class NonPreferenceFoodController {

    @Autowired
    private NonPreferenceFoodService nonPreferenceFoodService;

    // 선호 음식 전체 조회
    @GetMapping
    public ResponseEntity<?> getPreferences() {
        List<NonPreferenceFoodResponseDto> nonPreferences = nonPreferenceFoodService.getAllNonPreferences();
        return ResponseEntity.ok().body(new ApiResponseDto(200, "비선호 음식 조회 성공", nonPreferences));
    }

    // 선호 음식 추가
    @PostMapping
    public ResponseEntity<?> addPreference(@RequestBody NonPreferenceFoodDto nonPreferenceFoodDto) {
        NonPreferenceFoodResponseDto addedNonPreference = nonPreferenceFoodService.addNonPreference(nonPreferenceFoodDto);
        return ResponseEntity.ok().body(new ApiResponseDto(200, "비선호 음식 추가 성공", addedNonPreference));
    }

    // 선호 음식 삭제
    @DeleteMapping
    public ResponseEntity<?> deletePreference(@RequestBody NonPreferenceFoodDto nonPreferenceFoodDto) {
        nonPreferenceFoodService.deleteNonPreference(nonPreferenceFoodDto);
        return ResponseEntity.ok().body(new ApiResponseDto(200, "비선호 음식 삭제 성공", null));
    }
}
