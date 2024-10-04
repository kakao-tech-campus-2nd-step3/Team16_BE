package org.cookieandkakao.babting.domain.member.dto;

public record TokenIssueResponse(
    String accessToken,
    String refreshToken) {

}
