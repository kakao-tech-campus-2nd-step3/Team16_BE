package org.cookieandkakao.babting.domain.member.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberProfileGetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    void 회원_프로필을_수정할_수_있다() {
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
    void 회원의_카카오_토큰을_수정할_수_있다() {
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