package org.cookieandkakao.babting.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberInfoGetResponse;
import org.cookieandkakao.babting.domain.member.dto.KakaoTokenGetResponse;
import org.cookieandkakao.babting.domain.member.dto.TokenIssueResponse;
import org.cookieandkakao.babting.domain.member.service.AuthService;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.cookieandkakao.babting.domain.member.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "인증", description = "로그인, 토큰 재발급 등 인증 관련 api입니다.")
@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private static final int REFRESH_TOKEN_EXPIRES_IN = 1000 * 60 * 60 * 24 * 14;  // 2주

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    public AuthController(AuthService authService, JwtUtil jwtUtil, MemberService memberService) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.memberService = memberService;
    }

    @GetMapping("/login")
    @Operation(summary = "로그인", description = "카카오 로그인 페이지로 리다이렉트 합니다.")
    @ApiResponse(responseCode = "302", description = "카카오 인증 페이지로 리다이렉트", content = @Content)
    public String login() {
        return "redirect:" + authService.getAuthUrl();
    }

    @GetMapping("/login/code/kakao")
    @Operation(summary = "로그인 결과", description = "카카오 로그인 결과에 따라 로그인 성공, 실패 페이지로 리다이렉트 합니다.")
    @ApiResponse(responseCode = "302", description = "로그인 성공, 실패 페이지로 리다이렉션", content = @Content)
    public String issueToken(
        @Parameter(description = "카카오 인가 코드") @RequestParam(name = "code") String authorizeCode,
        HttpServletResponse response) {

        KakaoTokenGetResponse kakaoTokenDto;
        KakaoMemberInfoGetResponse kakaoMemberInfoDto;

        try {
            kakaoTokenDto = authService.requestKakaoToken(authorizeCode);
            kakaoMemberInfoDto = authService.requestKakaoMemberInfo(kakaoTokenDto);
        } catch (Exception e) {
            return "redirect:/login/fail";  // 프론트 페이지 구현 후 수정 예정
        }

        Long memberId = memberService.saveMemberInfoAndKakaoToken(kakaoMemberInfoDto,
            kakaoTokenDto);
        TokenIssueResponse tokenDto = authService.issueToken(memberId);

        response.addCookie(createRefreshTokenCookie(tokenDto));

        return "redirect:/login/success";  // 프론트 페이지 구현 후 수정 예정
    }

    @ResponseBody
    @GetMapping("access-token")
    @Operation(summary = "접근 토큰 재발급", description = "접근 토큰을 Authorization 헤더로 재발급합니다.")
    @ApiResponse(responseCode = "200", description = "접근 토큰 발급 성공")
    public ResponseEntity<ApiResponseBody.SuccessBody<Void>> reissueToken(
        @Parameter(hidden = true) @CookieValue(required = false) String refreshToken,
        HttpServletResponse response) {

        Long memberId = Long.parseLong(jwtUtil.parseClaims(refreshToken).getSubject());
        TokenIssueResponse tokenDto = authService.issueToken(memberId);

        response.setHeader("Authorization", tokenDto.accessToken());

        return ApiResponseGenerator.success(HttpStatus.OK, "토큰 재발급 성공");
    }

    private static Cookie createRefreshTokenCookie(TokenIssueResponse tokenDto) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.refreshToken());
        refreshTokenCookie.setPath("/api/auth/access-token");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(REFRESH_TOKEN_EXPIRES_IN);
        return refreshTokenCookie;
    }
}
