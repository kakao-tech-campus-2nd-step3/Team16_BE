package org.cookieandkakao.babting.domain.food.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.cookieandkakao.babting.common.annotaion.LoginMemberId;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.common.exception.customexception.InvalidFoodPreferenceTypeException;
import org.cookieandkakao.babting.domain.food.dto.FoodPreferenceGetResponse;
import org.cookieandkakao.babting.domain.food.dto.PersonalPreferenceUpdateRequest;
import org.cookieandkakao.babting.domain.food.service.MeetingFoodPreferenceStrategy;
import org.cookieandkakao.babting.domain.food.service.MeetingFoodPreferenceUpdater;
import org.cookieandkakao.babting.domain.food.service.MeetingNonPreferenceFoodService;
import org.cookieandkakao.babting.domain.food.service.MeetingPreferenceFoodService;
import org.cookieandkakao.babting.domain.food.service.MeetingPreferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meeting")
public class MemberMeetingFoodPreferenceController {
    private final Map<String, MeetingFoodPreferenceStrategy> strategies;
    private final MeetingFoodPreferenceUpdater meetingFoodPreferenceUpdater;
    private final MeetingPreferenceService meetingPreferenceService;

    public MemberMeetingFoodPreferenceController(
            MeetingPreferenceFoodService meetingPreferenceFoodService,
            MeetingNonPreferenceFoodService meetingNonPreferenceFoodService,
            MeetingFoodPreferenceUpdater meetingFoodPreferenceUpdater,
            MeetingPreferenceService meetingPreferenceService
    ) {
        this.meetingFoodPreferenceUpdater = meetingFoodPreferenceUpdater;
        this.meetingPreferenceService = meetingPreferenceService;
        strategies = Map.of(
                "preferences", meetingPreferenceFoodService,
                "non-preferences", meetingNonPreferenceFoodService
        );
    }

    @GetMapping("/{meeting_id}/{type}")
    @Operation(summary = "모임별 선호/비선호 음식 조회", description = "모임별 내 선호/비선호 음식을 가져옵니다.")
    @ApiResponse(responseCode = "200", description = "모임별 선호/비선호 음식 조회 성공")
    public ResponseEntity<ApiResponseBody.SuccessBody<List<FoodPreferenceGetResponse>>> getFoodPreferences(
            @PathVariable String type,
            @LoginMemberId Long memberId,
            @PathVariable("meeting_id") Long meetingId
    ) {
        MeetingFoodPreferenceStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new InvalidFoodPreferenceTypeException("잘못된 선호 타입입니다");
        }

        List<FoodPreferenceGetResponse> preferences = strategy.getAllPreferencesByMeeting(meetingId, memberId);

        if (preferences.isEmpty()) {
            return ApiResponseGenerator.success(HttpStatus.OK, "조회된 음식이 없습니다", null);
        }

        return ApiResponseGenerator.success(HttpStatus.OK, "모임별 개인 선호/비선호 음식 조회 성공", preferences);
    }

    @PutMapping("/{meeting_id}/personal")
    @Operation(summary = "모임별 선호/비선호 음식 수정", description = "모임별 내 선호/비선호 음식을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "모임별 선호/비선호 음식 수정 성공")
    public ResponseEntity<ApiResponseBody.SuccessBody<PersonalPreferenceUpdateRequest>> updatePreferences(
            @LoginMemberId Long memberId,
            @PathVariable("meeting_id") Long meetingId,
            @RequestBody PersonalPreferenceUpdateRequest PersonalPreferenceRequestDto
    ) {
        meetingFoodPreferenceUpdater.updatePreferences(
                meetingId,
                memberId,
                PersonalPreferenceRequestDto.preferences(),
                PersonalPreferenceRequestDto.nonPreferences()
        );

        return ApiResponseGenerator.success(HttpStatus.OK, "모임별 개인 정보 수정 성공", PersonalPreferenceRequestDto);
    }

    @GetMapping("/{meetingId}/recommend")
    @Operation(summary = "모임별 추천 음식 조회", description = "모임별 추천 음식을 가져옵니다.")
    @ApiResponse(responseCode = "200", description = "모임별 추천 음식 조회 성공")
    public ResponseEntity<ApiResponseBody.SuccessBody<List<FoodPreferenceGetResponse>>> getRecommendedFoodsForMeeting(
            @PathVariable Long meetingId
    ) {

        List<FoodPreferenceGetResponse> recommendedFoods = meetingPreferenceService.getRecommendedFoodDetailsForMeeting(meetingId);

        return ApiResponseGenerator.success(HttpStatus.OK, "모임 추천 음식 조회 성공", recommendedFoods);
    }
}
