package org.cookieandkakao.babting.domain.member.entity;

import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.cookieandkakao.babting.domain.member.dto.KakaoTokenGetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KakaoTokenTest {

    @Test
    void 카카오_접근_토큰을_수정할_수_있다() {
        // given
        KakaoToken kakaoToken = new KakaoToken("accessToken", LocalDateTime.now(), "refreshToken",
            LocalDateTime.now());
        KakaoTokenGetResponse kakaoTokenDto = new KakaoTokenGetResponse("new accessToken", 100,
            null, null);

        // when
        kakaoToken.updateAccessToken(kakaoTokenDto);

        // then
        Assertions.assertThat(kakaoToken.getAccessToken()).isEqualTo(kakaoTokenDto.accessToken());
        Assertions.assertThat(kakaoToken.getExpiresAt()).isEqualToIgnoringNanos(LocalDateTime.now().plusSeconds(kakaoTokenDto.expiresIn()));
    }

}