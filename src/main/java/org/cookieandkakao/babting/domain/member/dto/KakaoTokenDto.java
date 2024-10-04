package org.cookieandkakao.babting.domain.member.dto;

import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.member.entity.KakaoToken;

public record KakaoTokenDto(
    String accessToken,
    LocalDateTime expiresAt,
    String refreshToken,
    LocalDateTime refreshTokenExpiresAt
) {

    public KakaoTokenDto {
    }

    public KakaoTokenDto(KakaoToken kakaoToken) {
        this(kakaoToken.getAccessToken(), kakaoToken.getExpiresAt(), kakaoToken.getRefreshToken(),
            kakaoToken.getRefreshTokenExpiresAt());
    }
}
