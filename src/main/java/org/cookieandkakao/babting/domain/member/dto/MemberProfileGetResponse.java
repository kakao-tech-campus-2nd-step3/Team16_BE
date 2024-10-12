package org.cookieandkakao.babting.domain.member.dto;

public record MemberProfileGetResponse(
        Long memberId,
        String nickname,
        String thumbnailImageUrl,
        String profileImageUrl
) { }
