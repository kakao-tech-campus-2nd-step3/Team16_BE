package org.cookieandkakao.babting.domain.member.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.cookieandkakao.babting.domain.member.dto.TokenIssueResponse;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final SecretKey key = SIG.HS256.key().build();
    private final long ACCESS_TOKEN_EXPIRES_IN = 1000 * 60 * 30L;  // 30분
    private final long REFRESH_TOKEN_EXPIRES_IN = 1000 * 60 * 60 * 24 * 14L;  // 2주

    private String generateToken(Long userId, Long expiresIn) {

        Date now = Date.from(Instant.now());
        Date expiredDate = new Date(now.getTime() + expiresIn);

        return Jwts.builder()
            .subject(String.valueOf(userId))
            .expiration(expiredDate)
            .signWith(key)
            .compact();
    }

    private String generateAccessToken(Long userId) {
        return generateToken(userId, ACCESS_TOKEN_EXPIRES_IN);
    }

    private String generateRefreshToken(Long userId) {
        return generateToken(userId, REFRESH_TOKEN_EXPIRES_IN);
    }

    public TokenIssueResponse issueToken(Long userId) {
        String accessToken = generateAccessToken(userId);
        String refreshToken = generateRefreshToken(userId);

        return new TokenIssueResponse(accessToken, refreshToken);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("만료된 토큰입니다.");
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }

}
