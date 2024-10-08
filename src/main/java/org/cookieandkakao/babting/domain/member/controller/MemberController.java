package org.cookieandkakao.babting.domain.member.controller;

import org.cookieandkakao.babting.common.annotaion.LoginMemberId;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.member.dto.MemberProfileGetResponse;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseBody.SuccessBody<MemberProfileGetResponse>> getMemberProfile(
        @LoginMemberId Long memberId) {
        MemberProfileGetResponse memberProfile = memberService.getMemberProfile(memberId);
        return ApiResponseGenerator.success(HttpStatus.OK, "프로필 조회 성공", memberProfile);
    }
}
