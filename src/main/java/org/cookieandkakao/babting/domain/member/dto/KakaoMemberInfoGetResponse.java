package org.cookieandkakao.babting.domain.member.dto;

public record KakaoMemberInfoGetResponse(
    Long id,
    KakaoMemberProfileGetResponse properties) {

}
