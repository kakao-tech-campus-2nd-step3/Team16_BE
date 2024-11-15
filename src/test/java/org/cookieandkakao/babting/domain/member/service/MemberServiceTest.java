package org.cookieandkakao.babting.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberInfoGetResponse;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberProfileGetResponse;
import org.cookieandkakao.babting.domain.member.dto.KakaoTokenGetResponse;
import org.cookieandkakao.babting.domain.member.dto.MemberProfileGetResponse;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.exception.MemberNotFoundException;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private MemberService memberService;

    private final String NICKNAME = "닉네임";
    private final String PROFILE_IMG = "profile.jpg";
    private final String THUMBNAIL_IMG = "thumbnail.jpg";
    private final String ACCESS_TOKEN = "accessToken";
    private final String REFRESH_TOKEN = "refreshToken";

    @Test
    void 회원_번호로_회원을_찾을_수_있다() {
        // given
        Member member = new Member(1L);
        given(memberRepository.findById(any())).willReturn(Optional.of(member));

        // when
        Member actual = memberService.findMember(1L);

        // then
        assertThat(actual).isEqualTo(member);
    }

    @Test
    void 존재하지_않는_회원_번호로_회원을_찾을_경우_예외_발생() {
        // given
        given(memberRepository.findById(any())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> memberService.findMember(1L)).isInstanceOf(
            MemberNotFoundException.class);
    }

    @Test
    void 회원_프로필을_조회할_수_있다() {
        // given
        Long memberId = 1L;
        KakaoMemberProfileGetResponse kakaoProfile = getKakaoProfile();
        Member member = new Member(memberId);
        member.updateProfile(kakaoProfile);
        given(memberRepository.findById(any())).willReturn(Optional.of(member));

        // when
        MemberProfileGetResponse memberProfile = memberService.getMemberProfile(memberId);

        // then
        assertThat(memberProfile.memberId()).isEqualTo(memberId);
        assertThat(memberProfile.nickname()).isEqualTo(NICKNAME);
        assertThat(memberProfile.profileImageUrl()).isEqualTo(PROFILE_IMG);
        assertThat(memberProfile.thumbnailImageUrl()).isEqualTo(THUMBNAIL_IMG);
    }

    @Test
    void 회원_정보와_카카오_토큰을_저장_할_수_있다() {
        // given
        Long kakaoMemberId = 1L;
        Member member = new Member(kakaoMemberId);
        KakaoMemberInfoGetResponse kakaoMemberInfo = getKakaoMemberInfo(kakaoMemberId);
        KakaoTokenGetResponse kakaoTokenResponse = getKakaoTokenResponse();

        given(memberRepository.findByKakaoMemberId(any())).willReturn(Optional.of(member));
        given(memberRepository.save(any())).willReturn(member);

        // when
        memberService.saveMemberInfoAndKakaoToken(kakaoMemberInfo, kakaoTokenResponse);

        // then
        assertThat(member.getNickname()).isEqualTo(kakaoMemberInfo.properties().nickname());
        assertThat(member.getProfileImageUrl())
            .isEqualTo(kakaoMemberInfo.properties().profileImage());
        assertThat(member.getThumbnailImageUrl())
            .isEqualTo(kakaoMemberInfo.properties().thumbnailImage());
        assertThat(member.getKakaoToken().getAccessToken()).isEqualTo(
            kakaoTokenResponse.accessToken());
        assertThat(member.getKakaoToken().getRefreshToken()).isEqualTo(
            kakaoTokenResponse.refreshToken());
    }

    private KakaoMemberProfileGetResponse getKakaoProfile() {
        return new KakaoMemberProfileGetResponse(NICKNAME, PROFILE_IMG, THUMBNAIL_IMG);
    }

    private KakaoMemberInfoGetResponse getKakaoMemberInfo(Long kakaoMemberId) {
        return new KakaoMemberInfoGetResponse(kakaoMemberId, getKakaoProfile());
    }

    private KakaoTokenGetResponse getKakaoTokenResponse() {
        return new KakaoTokenGetResponse(ACCESS_TOKEN, 1, REFRESH_TOKEN, 1);
    }

}