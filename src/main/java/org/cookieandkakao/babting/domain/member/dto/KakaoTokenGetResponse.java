package org.cookieandkakao.babting.domain.member.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.member.entity.KakaoToken;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoTokenGetResponse(
    String accessToken,
    Integer expiresIn,
    String refreshToken,
    Integer refreshTokenExpiresIn) {

    public KakaoToken toEntity() {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime expiresAt = now.plusSeconds(expiresIn);
        LocalDateTime refreshTokenExpiresAt = now.plusSeconds(refreshTokenExpiresIn);

        return new KakaoToken(this.accessToken, expiresAt, this.refreshToken,
            refreshTokenExpiresAt);
    }
}
