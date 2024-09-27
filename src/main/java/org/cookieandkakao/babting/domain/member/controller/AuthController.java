package org.cookieandkakao.babting.domain.member.controller;

import org.cookieandkakao.babting.domain.member.dto.KakaoMemberInfoDto;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberProfileDto;
import org.cookieandkakao.babting.domain.member.dto.KakaoTokenDto;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    public AuthController(AuthService authService, MemberRepository memberRepository) {
        this.authService = authService;
        this.memberRepository = memberRepository;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("redirect:" + authService.getAuthUrl());
    }

    @GetMapping("/login/code/kakao")
    public KakaoMemberProfileDto getToken(@RequestParam(name = "code") String authorizeCode) {

        KakaoTokenDto kakaoTokenDto = authService.requestKakaoToken(authorizeCode);

        KakaoMemberInfoDto kakaoMemberInfoDto = authService.requestKakaoMemberInfo(kakaoTokenDto);

        authService.saveMemberInfo(kakaoMemberInfoDto);
        authService.saveKakaoToken(kakaoMemberInfoDto.getId(), kakaoTokenDto);

        return kakaoMemberInfoDto.getProperties();
    }
}
