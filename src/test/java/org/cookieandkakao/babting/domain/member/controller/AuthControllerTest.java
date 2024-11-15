package org.cookieandkakao.babting.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.cookieandkakao.babting.common.exception.customexception.ApiException;
import org.cookieandkakao.babting.domain.member.dto.TokenIssueResponse;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.cookieandkakao.babting.domain.member.service.AuthService;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.cookieandkakao.babting.domain.member.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(JwtUtil.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String REFRESH_TOKEN = "refreshToken";

    @Test
    void 카카오_로그인_리다이렉트() throws Exception {
        // given
        String authUrl = "https://test-auth-url.com";

        given(authService.getAuthUrl())
            .willReturn(authUrl);

        // when
        // then
        mockMvc.perform(get("/api/auth/login"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("location", authUrl));
    }

    @Test
    void 로그인_성공시_토큰_발급() throws Exception {
        // given
        String refreshToken = jwtUtil.issueToken(1L).refreshToken();

        given(authService.issueToken(any()))
            .willReturn(new TokenIssueResponse("accessToken", refreshToken));

        // when
        // then
        mockMvc.perform(get("/api/auth/login/code/kakao").param("code", "testcode"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("location", "https://www.babting.com/success"))
            .andExpect(cookie().value(REFRESH_TOKEN, refreshToken))
            .andExpect(cookie().path(REFRESH_TOKEN, "/api/auth/access-token"))
            .andExpect(cookie().httpOnly(REFRESH_TOKEN, true));
    }

    @Test
    void 인가_코드가_없을시_로그인_실패() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/auth/login/code/kakao"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void 카카오_토큰_발급_실패시_로그인_실패() throws Exception {
        // given
        given(authService.requestKakaoToken(any()))
            .willThrow(new ApiException("카카오 토큰 발급 실패"));

        // when
        // then
        mockMvc.perform(get("/api/auth/login/code/kakao").param("code", "testcode"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("location", "https://www.babting.com/failure"));
    }

    @Test
    void 카카오_회원_정보_조회_실패시_로그인_실패() throws Exception {
        // given
        given(authService.requestKakaoToken(any()))
            .willThrow(new ApiException("카카오 회원 정보 조회 실패"));

        // when
        // then
        mockMvc.perform(get("/api/auth/login/code/kakao").param("code", "testcode"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("location", "https://www.babting.com/failure"));
    }

    @Test
    void 갱신_토큰이_유효할_경우_접근_토큰_재발급() throws Exception {
        // given
        Long memberId = 1L;
        TokenIssueResponse tokenIssueResponse = jwtUtil.issueToken(memberId);
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN, tokenIssueResponse.refreshToken());

        given(authService.issueToken(anyLong())).willReturn(tokenIssueResponse);

        // when
        // then
        mockMvc.perform(get("/api/auth/access-token").cookie(refreshTokenCookie))
            .andExpect(status().isOk())
            .andExpect(header().exists("Authorization"));
    }

    @Test
    void 갱신_토큰이_유효하지_않을_경우_접근_토큰_재발급_실패() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/auth/access-token"))
            .andExpect(status().isUnauthorized());
    }

}