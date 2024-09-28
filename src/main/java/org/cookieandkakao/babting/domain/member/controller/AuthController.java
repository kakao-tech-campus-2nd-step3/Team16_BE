package org.cookieandkakao.babting.domain.member.controller;

import org.cookieandkakao.babting.domain.member.dto.KakaoMemberInfoGetResponseDto;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberProfileGetResponseDto;
import org.cookieandkakao.babting.domain.member.dto.KakaoTokenGetResponseDto;
import org.cookieandkakao.babting.domain.member.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("redirect:" + authService.getAuthUrl());
    }

    @GetMapping("/login/code/kakao")
    public KakaoMemberProfileGetResponseDto getToken(
        @RequestParam(name = "code") String authorizeCode) {

        KakaoTokenGetResponseDto kakaoTokenGetResponseDto = authService.requestKakaoToken(
            authorizeCode);

        KakaoMemberInfoGetResponseDto kakaoMemberInfoGetResponseDto = authService.requestKakaoMemberInfo(
            kakaoTokenGetResponseDto);

        authService.saveMemberInfo(kakaoMemberInfoGetResponseDto);
        authService.saveKakaoToken(kakaoMemberInfoGetResponseDto.id(), kakaoTokenGetResponseDto);

        return kakaoMemberInfoGetResponseDto.properties();
    }
}
