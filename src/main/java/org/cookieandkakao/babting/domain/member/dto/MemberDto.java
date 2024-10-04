package org.cookieandkakao.babting.domain.member.dto;

public record MemberDto(
    Long memberId,
    String nickname,
    String thumbnailImageUrl,
    String profileImageUrl,
    KakaoTokenDto kakaoToken
) {

}
