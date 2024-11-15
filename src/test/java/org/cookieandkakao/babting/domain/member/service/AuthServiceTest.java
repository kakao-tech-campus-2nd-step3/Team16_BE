package org.cookieandkakao.babting.domain.member.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.cookieandkakao.babting.common.properties.KakaoClientProperties;
import org.cookieandkakao.babting.common.properties.KakaoProviderProperties;
import org.cookieandkakao.babting.domain.member.dto.KakaoTokenGetResponse;
import org.cookieandkakao.babting.domain.member.dto.TokenIssueResponse;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.exception.MemberNotFoundException;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.cookieandkakao.babting.domain.member.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private KakaoClientProperties kakaoClientProperties;

    @Mock
    private KakaoProviderProperties kakaoProviderProperties;

    @Mock
    private KakaoAuthClient kakaoAuthClient;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void 카카오_OAuth_토큰을_요청할_수_있다() {
        // given
        // when
        authService.requestKakaoToken("authCode");

        // then
        then(kakaoAuthClient).should().callTokenApi(any());
    }

    @Test
    void 카카오_OAuth_토큰_재발급_요청을_할_수_있다() {
        // given
        // when
        authService.refreshKakaoToken("authCode");

        // then
        then(kakaoAuthClient).should().callTokenApi(any());
    }

    @Test
    void 카카오_회원_정보를_요청할_수_있다() {
        // given
        // when
        authService.requestKakaoMemberInfo(
            new KakaoTokenGetResponse("accessToken", 1, "refreshToken", 1));

        // then
        then(kakaoAuthClient).should().callMemberInfoApi(any());
    }

    @Test
    void 카카오_회원_연결_해제를_요청할_수_있다() {
        // given
        // when
        authService.unlinkMember("accessToken");

        // then
        then(kakaoAuthClient).should().callUnlinkApi(any());
    }

    @Test
    void 밥팅_토큰을_발급할_수_있다() {
        // given
        Long memberId = 1L;
        given(memberRepository.findById(any())).willReturn(Optional.of(new Member(1L)));

        // when
        TokenIssueResponse tokens = authService.issueToken(memberId);

        // then
        then(jwtUtil).should().issueToken(any());
    }

    @Test
    void 존재하지_않는_회원번호로_밥팅_토큰_발급_요청시_예외가_발생한다() {
        // given
        Long memberId = 1L;
        given(memberRepository.findById(any())).willReturn(Optional.empty());

        // when
        // then
        Assertions.assertThatThrownBy(() -> authService.issueToken(memberId))
            .isInstanceOf(MemberNotFoundException.class);
    }
}