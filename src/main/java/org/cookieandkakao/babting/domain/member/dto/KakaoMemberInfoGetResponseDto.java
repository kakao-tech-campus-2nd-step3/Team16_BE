package org.cookieandkakao.babting.domain.member.dto;

public record KakaoMemberInfoGetResponseDto(
    Long id,
    KakaoMemberProfileGetResponseDto properties) {

}
