package org.cookieandkakao.babting.domain.meeting.controller;

import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.SuccessBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.food.service.FoodService;
import org.cookieandkakao.babting.domain.meeting.dto.request.ConfirmDateTimeGetRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingCreateRequest;
import org.cookieandkakao.babting.domain.meeting.service.MeetingService;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meeting")
public class MeetingController {
    private final MeetingService meetingService;
    private final FoodService foodService;

    public MeetingController(MeetingService meetingService, FoodService foodService) {
        this.meetingService = meetingService;
        this.foodService = foodService;
    }

    // 모임 생성(주최자)
    @PostMapping
    public ResponseEntity<SuccessBody<Void>> createMeeting(
        Member member,
        MeetingCreateRequest meetingCreateRequest){
        meetingService.createMeeting(member, meetingCreateRequest);
        return ApiResponseGenerator.success(HttpStatus.CREATED, "모임 생성 성공");
    }

    // 모임 참가(초대받은사람)
    @PostMapping("/{meetingId}/join")
    public ResponseEntity<SuccessBody<Void>> joinMeeting(
        @PathVariable("meetingId") Long meetingId,
        Member member
    ){
        // Todo 지우님 전략 패턴 적용 후 코드 추가 예정
        return ApiResponseGenerator.success(HttpStatus.OK, "모임 참가 성공");
    }

    // 모임 시간 확정(주최자)
    @PostMapping("/{meetingId}/confirm")
    public ResponseEntity<SuccessBody<Void>> decideMeeting(
        @PathVariable("meetingId") Long meetingId,
        Member member,
        ConfirmDateTimeGetRequest confirmDateTimeGetRequest
    ){
        meetingService.decideMeetingTime(member, confirmDateTimeGetRequest.confirmDateTime(), meetingId);
        return ApiResponseGenerator.success(HttpStatus.OK, "모임 시간 확정 성공");
    }

    // 모임 탈퇴(주최자, 초대받은 사람)
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<SuccessBody<Void>> exitMeeting(
        @PathVariable("meetingId") Long meetingId,
        Member member
    ){
        meetingService.exitMeeting(member, meetingId);
        return ApiResponseGenerator.success(HttpStatus.OK, "모임 탈퇴 성공");
    }

    // 참여 모임 목록 조회
//    @GetMapping
//    public ResponseEntity<SuccessBody<MeetingGetResponse>> getAllMeeting(
//        Member member
//    ){
//
//    }
}
