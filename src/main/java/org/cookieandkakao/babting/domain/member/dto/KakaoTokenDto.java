package org.cookieandkakao.babting.domain.member.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.member.entity.KakaoToken;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoTokenDto {

    private String accessToken;
    private Integer expiresIn;
    private String refreshToken;
    private Integer refreshTokenExpiresIn;

    public KakaoTokenDto(String accessToken, Integer expiresIn,
        String refreshToken, Integer refreshTokenExpiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Integer getRefreshTokenExpiresIn() {
        return refreshTokenExpiresIn;
    }

    public KakaoToken toEntity() {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime expiresAt = now.plusSeconds(expiresIn);
        LocalDateTime refreshTokenExpiresAt = now.plusSeconds(refreshTokenExpiresIn);

        return new KakaoToken(this.accessToken, expiresAt, this.refreshToken,
            refreshTokenExpiresAt);
    }
}
