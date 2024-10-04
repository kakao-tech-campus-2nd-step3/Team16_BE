package org.cookieandkakao.babting.domain.member.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberInfoGetResponse;
import org.cookieandkakao.babting.domain.member.dto.KakaoTokenGetResponse;
import org.cookieandkakao.babting.domain.member.dto.TokenIssueResponse;
import org.cookieandkakao.babting.domain.member.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:" + authService.getAuthUrl();
    }

    @GetMapping("/login/code/kakao")
    public String issueToken(
        @RequestParam(name = "code") String authorizeCode, HttpServletResponse response) {

        KakaoTokenGetResponse kakaoTokenDto;
        KakaoMemberInfoGetResponse kakaoMemberInfoDto;

        try {
            kakaoTokenDto = authService.requestKakaoToken(authorizeCode);
            kakaoMemberInfoDto = authService.requestKakaoMemberInfo(kakaoTokenDto);
        } catch (Exception e) {
            return "redirect:/login/fail";  // 프론트 페이지 구현 후 수정 예정
        }

        authService.saveMemberInfoAndKakaoToken(kakaoMemberInfoDto, kakaoTokenDto);
        TokenIssueResponse tokenDto = authService.issueToken(kakaoMemberInfoDto.id());

        Cookie accessTokenCookie = new Cookie("accessToken", tokenDto.accessToken());
        accessTokenCookie.setPath("/");

        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.refreshToken());
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 14); // 2주

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return "redirect:/login/success";  // 프론트 페이지 구현 후 수정 예정
    }
}
