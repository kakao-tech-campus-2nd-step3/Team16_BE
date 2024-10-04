package org.cookieandkakao.babting.domain.member.service;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import org.cookieandkakao.babting.common.properties.KakaoClientProperties;
import org.cookieandkakao.babting.common.properties.KakaoProviderProperties;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberInfoGetResponseDto;
import org.cookieandkakao.babting.domain.member.dto.KakaoTokenGetResponseDto;
import org.cookieandkakao.babting.domain.member.dto.TokenIssueResponseDto;
import org.cookieandkakao.babting.domain.member.entity.KakaoToken;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.repository.KakaoTokenRepository;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.cookieandkakao.babting.domain.member.util.AuthorizationUriBuilder;
import org.cookieandkakao.babting.domain.member.util.JwtUtil;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class AuthService {

    private final KakaoClientProperties kakaoClientProperties;
    private final KakaoProviderProperties kakaoProviderProperties;
    private final RestClient restClient = RestClient.builder().build();
    private final MemberRepository memberRepository;
    private final KakaoTokenRepository kakaoTokenRepository;
    private final JwtUtil jwtUtil;

    public AuthService(KakaoClientProperties kakaoClientProperties,
        KakaoProviderProperties kakaoProviderProperties, MemberRepository memberRepository,
        KakaoTokenRepository kakaoTokenRepository, JwtUtil jwtUtil) {
        this.kakaoClientProperties = kakaoClientProperties;
        this.kakaoProviderProperties = kakaoProviderProperties;
        this.memberRepository = memberRepository;
        this.kakaoTokenRepository = kakaoTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    public String getAuthUrl() {
        return new AuthorizationUriBuilder()
            .clientProperties(kakaoClientProperties)
            .providerProperties(kakaoProviderProperties)
            .build();
    }

    public KakaoTokenGetResponseDto requestKakaoToken(String authorizeCode) {

        String tokenUri = kakaoProviderProperties.tokenUri();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", authorizeCode);
        body.add("redirect_uri", kakaoClientProperties.redirectUri());
        body.add("client_id", kakaoClientProperties.clientId());
        body.add("client_secret", kakaoClientProperties.clientSecret());

        ResponseEntity<KakaoTokenGetResponseDto> entity = restClient.post()
            .uri(tokenUri)
            .contentType(APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                throw new IllegalArgumentException("카카오 토큰 발급 실패");
            })
            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                throw new RuntimeException("카카오 인증 서버 에러");
            })
            .toEntity(KakaoTokenGetResponseDto.class);

        return entity.getBody();
    }

    public KakaoMemberInfoGetResponseDto requestKakaoMemberInfo(
        KakaoTokenGetResponseDto kakaoToken) {

        String userInfoUri = kakaoProviderProperties.userInfoUri();

        ResponseEntity<KakaoMemberInfoGetResponseDto> entity = restClient.get()
            .uri(userInfoUri)
            .header("Authorization", "Bearer " + kakaoToken.accessToken())
            .header("Content-Type", "application/x-www-form-urlencoded")
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                throw new IllegalArgumentException("카카오 사용자 정보 조회 실패");
            })
            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                throw new RuntimeException("카카오 사용자 정보 서버 에러");
            })
            .toEntity(KakaoMemberInfoGetResponseDto.class);

        return entity.getBody();
    }

    @Transactional
    public void saveMemberInfo(KakaoMemberInfoGetResponseDto kakaoMemberInfoGetResponseDto) {

        Long kakaoMemberId = kakaoMemberInfoGetResponseDto.id();

        Member member = memberRepository.findByKakaoMemberId(kakaoMemberId)
            .orElse(new Member(kakaoMemberId));
        member.updateProfile(kakaoMemberInfoGetResponseDto.properties());

        memberRepository.save(member);
    }

    @Transactional
    public void saveKakaoToken(Long kakaoMemberId,
        KakaoTokenGetResponseDto kakaoTokenGetResponseDto) {

        Member member = memberRepository.findByKakaoMemberId(kakaoMemberId)
            .orElseThrow(IllegalArgumentException::new);
        KakaoToken kakaoToken = kakaoTokenGetResponseDto.toEntity();

        kakaoTokenRepository.save(kakaoToken);

        member.updateKakaoToken(kakaoToken);
    }

    public TokenIssueResponseDto issueToken(Long kakaoMemberId) {
        Member member = memberRepository.findByKakaoMemberId(kakaoMemberId)
            .orElseThrow(IllegalArgumentException::new);

        return jwtUtil.issueToken(member.getMemberId());
    }
}
