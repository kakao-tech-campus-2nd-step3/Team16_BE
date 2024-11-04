package org.cookieandkakao.babting.domain.member.service;

import org.cookieandkakao.babting.common.properties.KakaoClientProperties;
import org.cookieandkakao.babting.common.properties.KakaoProviderProperties;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberInfoGetResponse;
import org.cookieandkakao.babting.domain.member.dto.KakaoTokenGetResponse;
import org.cookieandkakao.babting.domain.member.dto.TokenIssueResponse;
import org.cookieandkakao.babting.domain.member.entity.KakaoToken;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.cookieandkakao.babting.domain.member.util.AuthorizationUriBuilder;
import org.cookieandkakao.babting.domain.member.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class AuthService {

    private final KakaoClientProperties kakaoClientProperties;
    private final KakaoProviderProperties kakaoProviderProperties;
    private final KakaoAuthClient kakaoAuthClient;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public AuthService(KakaoClientProperties kakaoClientProperties,
        KakaoProviderProperties kakaoProviderProperties, KakaoAuthClient kakaoAuthClient,
        MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.kakaoClientProperties = kakaoClientProperties;
        this.kakaoProviderProperties = kakaoProviderProperties;
        this.kakaoAuthClient = kakaoAuthClient;
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    public String getAuthUrl() {
        return new AuthorizationUriBuilder()
            .clientProperties(kakaoClientProperties)
            .providerProperties(kakaoProviderProperties)
            .build();
    }

    public KakaoTokenGetResponse requestKakaoToken(String authorizeCode) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", authorizeCode);
        body.add("redirect_uri", kakaoClientProperties.redirectUri());
        body.add("client_id", kakaoClientProperties.clientId());
        body.add("client_secret", kakaoClientProperties.clientSecret());

        return kakaoAuthClient.callTokenApi(body);
    }

    public KakaoTokenGetResponse refreshKakaoToken(String refreshToekn) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", kakaoClientProperties.clientId());
        body.add("refresh_token", refreshToekn);

        return kakaoAuthClient.callTokenApi(body);
    }

    public KakaoMemberInfoGetResponse requestKakaoMemberInfo(
        KakaoTokenGetResponse kakaoTokenDto) {

        return kakaoAuthClient.callMemberInfoApi(kakaoTokenDto.accessToken());
    }

    public void unlinkMember(String accessToken) {

        kakaoAuthClient.callUnlinkApi(accessToken);
    }

    public TokenIssueResponse issueToken(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(IllegalArgumentException::new);

        return jwtUtil.issueToken(member.getMemberId());
    }
}
