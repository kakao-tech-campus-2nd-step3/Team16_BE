package org.cookieandkakao.babting.domain.member.dto;

public record TokenIssueResponseDto (
    String accessToken,
    String refreshToken) {

}
