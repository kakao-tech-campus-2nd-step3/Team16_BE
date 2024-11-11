package org.cookieandkakao.babting.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cookieandkakao.babting.common.annotaion.LoginMemberId;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.member.dto.MemberProfileGetResponse;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원", description = "회원 관련 api입니다.")
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    @Operation(summary = "회원 프로필 조회", description = "현재 로그인된 회원 본인의 프로필을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "로그인된 회원 본인의 프로필 정보")
    public ResponseEntity<ApiResponseBody.SuccessBody<MemberProfileGetResponse>> getMemberProfile(
        @LoginMemberId Long memberId) {
        MemberProfileGetResponse memberProfile = memberService.getMemberProfile(memberId);
        return ApiResponseGenerator.success(HttpStatus.OK, "프로필 조회 성공", memberProfile);
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴합니다.")
    @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공 메세지")
    public ResponseEntity<ApiResponseBody.SuccessBody<Void>> deleteMember(
        @LoginMemberId Long memberId) {
        memberService.deleteMember(memberId);
        return ApiResponseGenerator.success(HttpStatus.OK, "회원 탈퇴 성공");
    }
}
