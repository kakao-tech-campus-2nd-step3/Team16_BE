package org.cookieandkakao.babting.domain.member.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberProfileGetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    @DisplayName("회원 프로필을 수정할 수 있다")
    void updateProfile() {
        // given
        Member member = new Member(1L);
        KakaoMemberProfileGetResponse profileDto = new KakaoMemberProfileGetResponse(
            "nickname", "pfImg.jpg", "tnImg.jpg");

        // when
        member.updateProfile(profileDto);

        // then
        assertThat(member.getNickname()).isEqualTo(profileDto.nickname());
        assertThat(member.getProfileImageUrl()).isEqualTo(profileDto.profileImage());
        assertThat(member.getThumbnailImageUrl()).isEqualTo(profileDto.thumbnailImage());
    }

    @Test
    @DisplayName("회원의 카카오 토큰을 수정할 수 있다")
    void updateKakaoToken() {
        // given
        Member member = new Member(1L);
        KakaoToken kakaoToken = new KakaoToken("accessToken", LocalDateTime.now(), "refreshToken",
            LocalDateTime.now());

        // when
        member.updateKakaoToken(kakaoToken);

        // then
        assertThat(member.getKakaoToken()).isEqualTo(kakaoToken);
    }

}