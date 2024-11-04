package org.cookieandkakao.babting.domain.meeting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.cookieandkakao.babting.common.annotaion.LoginMemberId;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.SuccessBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.meeting.dto.request.ConfirmMeetingGetRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingJoinCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingTimeCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.response.MeetingGetResponse;
import org.cookieandkakao.babting.domain.meeting.dto.response.MeetingHostCheckResponse;
import org.cookieandkakao.babting.domain.meeting.dto.response.MeetingInfoGetResponse;
import org.cookieandkakao.babting.domain.meeting.dto.response.TimeAvailableGetResponse;
import org.cookieandkakao.babting.domain.meeting.service.MeetingEventService;
import org.cookieandkakao.babting.domain.meeting.service.MeetingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meeting")
@Tag(name = "모임 관련 api", description = "모임 관련한 api입니다.")
public class MeetingController {
    private final MeetingService meetingService;
    private final MeetingEventService meetingEventService;

    public MeetingController(MeetingService meetingService,
        MeetingEventService meetingEventService) {
        this.meetingService = meetingService;
        this.meetingEventService = meetingEventService;
    }

    // 모임 생성(주최자)
    @PostMapping
    @Operation(summary = "모임 생성", description = "새 모임을 생성합니다")
    @ApiResponse(responseCode = "201", description = "모임 생성 성공")
    public ResponseEntity<SuccessBody<Void>> createMeeting(
        @LoginMemberId Long memberId,
        @RequestBody MeetingCreateRequest meetingCreateRequest){
        meetingService.createMeeting(memberId, meetingCreateRequest);
        return ApiResponseGenerator.success(HttpStatus.CREATED, "모임 생성 성공");
    }

    // 모임 참가(초대받은사람)
    @PostMapping("/{meetingId}/join")
    @Operation(summary = "모임 참가", description = "특정 모임에 참가합니다. 모임만의 선호 / 비선호 음식 정보를 입력합니다.")
    @ApiResponse(responseCode = "200", description = "모임 참가 성공")
    public ResponseEntity<SuccessBody<Void>> joinMeeting(
        @PathVariable("meetingId") Long meetingId,
        @LoginMemberId Long memberId,
        @RequestBody MeetingJoinCreateRequest meetingJoinCreateRequest
    ){
        meetingService.joinMeeting(memberId, meetingId);
        meetingEventService.saveMeetingAvoidTime(memberId, meetingId, meetingJoinCreateRequest.times());
        return ApiResponseGenerator.success(HttpStatus.OK, "모임 참가 성공");
    }

    // 모임 확정(주최자)
    @PostMapping("/{meetingId}/confirm")
    @Operation(summary = "모임 확정", description = "모임을 확정합니다.")
    @ApiResponse(responseCode = "202", description = "모임 확정 성공")
    public ResponseEntity<SuccessBody<Void>> confirmMeeting(
        @PathVariable("meetingId") Long meetingId,
        @RequestBody ConfirmMeetingGetRequest confirmMeetingGetRequest,
        @LoginMemberId Long memberId
    ) {
        meetingEventService.confirmMeeting(memberId, meetingId, confirmMeetingGetRequest);
        return ApiResponseGenerator.success(HttpStatus.ACCEPTED, "모임 확정 성공");
    }

    // 모임 탈퇴(주최자, 초대받은 사람)
    @DeleteMapping("/{meetingId}")
    @Operation(summary = "모임 탈퇴", description = "모임을 탈퇴합니다.")
    @ApiResponse(responseCode = "202", description = "모임 탈퇴 성공")
    public ResponseEntity<SuccessBody<Void>> exitMeeting(
        @PathVariable("meetingId") Long meetingId,
        @LoginMemberId Long memberId
    ){
        meetingService.exitMeeting(memberId, meetingId);
        return ApiResponseGenerator.success(HttpStatus.ACCEPTED, "모임 탈퇴 성공");
    }

    // 내가 참여한 모임 목록 조회
    @GetMapping
    @Operation(summary = "내가 참여한 모임 목록 조회", description = "내가 참여한 모임의 목록을 가져옵니다.")
    @ApiResponse(responseCode = "200", description = "참여 모임 목록 조회 성공")
    public ResponseEntity<SuccessBody<List<MeetingGetResponse>>> getAllMeeting(
        @LoginMemberId Long memberId
    ){
        List<MeetingGetResponse> meetings = meetingService.getAllMeetings(memberId);
        return ApiResponseGenerator.success(HttpStatus.OK, "참여 모임 목록 조회 성공", meetings);
    }

    // 모임 id로 이름, 모임 시작과 끝 날짜 조회
    @GetMapping("/{meetingId}")
    @Operation(summary = "모임 정보 조회", description = "모임의 제목, 모임의 시작과 끝 날짜를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "모임 정보 조회 성공")
    public ResponseEntity<SuccessBody<MeetingInfoGetResponse>> getMeetingInfo(
        @PathVariable("meetingId") Long meetingId
    ){
        MeetingInfoGetResponse meetingInfo = meetingService.getMeetingInfo(meetingId);
        return ApiResponseGenerator.success(HttpStatus.OK, "모임 정보 조회 성공", meetingInfo);
    }
    // 모임 주최자 확인
    @GetMapping("/{meetingId}/is-host")
    @Operation(summary = "모임 주최자 확인", description = "해당 meetingid의 모임 주최자인지 확인합니다.")
    @ApiResponse(responseCode = "200", description = "주최자 여부 확인 성공")
    public ResponseEntity<SuccessBody<MeetingHostCheckResponse>> getIsHost(
        @LoginMemberId Long memberId,
        @PathVariable("meetingId") Long meetingId
    ){
        MeetingHostCheckResponse isHost = meetingService.checkHost(memberId,
            meetingId);
        return ApiResponseGenerator.success(HttpStatus.OK, "주최자 여부 확인 성공", isHost);
    }
    // 모임 확정 날짜, 확정 음식 확인

    // 모임 공통 빈 시간 조회
    @GetMapping("/{meetingId}/calendar")
    @Operation(summary = "모임 공통 빈 시간 조회", description = "모임에 참여한 멤버들의 공통 시간표를 조회합니다. (모두가 비어있는 시간)")
    @ApiResponse(responseCode = "200", description = "모임 공통 시간표 조회 성공")
    public ResponseEntity<SuccessBody<TimeAvailableGetResponse>> getAvailableTime(
        @PathVariable("meetingId") Long meetingId
    ){
        TimeAvailableGetResponse timeAvailableGetResponse = meetingEventService.findAvailableTime(meetingId);
        return ApiResponseGenerator.success(HttpStatus.OK, "모임 공통 시간표 조회 성공", timeAvailableGetResponse);
    }
    // 모임 정보 수정
}
