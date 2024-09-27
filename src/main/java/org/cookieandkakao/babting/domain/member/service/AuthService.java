package org.cookieandkakao.babting.domain.member.service;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import org.cookieandkakao.babting.domain.member.dto.KakaoMemberInfoDto;
import org.cookieandkakao.babting.domain.member.dto.KakaoTokenDto;
import org.cookieandkakao.babting.domain.member.entity.KakaoToken;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.properties.KakaoClientProperties;
import org.cookieandkakao.babting.domain.member.properties.KakaoProviderProperties;
import org.cookieandkakao.babting.domain.member.repository.KakaoTokenRepository;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.cookieandkakao.babting.domain.member.util.AuthorizationUriBuilder;
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

    public AuthService(KakaoClientProperties kakaoClientProperties,
        KakaoProviderProperties kakaoProviderProperties, MemberRepository memberRepository,
        KakaoTokenRepository kakaoTokenRepository) {
        this.kakaoClientProperties = kakaoClientProperties;
        this.kakaoProviderProperties = kakaoProviderProperties;
        this.memberRepository = memberRepository;
        this.kakaoTokenRepository = kakaoTokenRepository;
    }

    public String getAuthUrl() {
        return new AuthorizationUriBuilder()
            .clientProperties(kakaoClientProperties)
            .providerProperties(kakaoProviderProperties)
            .build();
    }

    public KakaoTokenDto requestKakaoToken(String authorizeCode) {

        String tokenUri = kakaoProviderProperties.tokenUri();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", authorizeCode);
        body.add("redirect_uri", kakaoClientProperties.redirectUri());
        body.add("client_id", kakaoClientProperties.clientId());
        body.add("client_secret", kakaoClientProperties.clientSecret());

        ResponseEntity<KakaoTokenDto> entity = restClient.post()
            .uri(tokenUri)
            .contentType(APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .toEntity(KakaoTokenDto.class);

        return entity.getBody();

    }

    public KakaoMemberInfoDto requestKakaoMemberInfo(KakaoTokenDto kakaoToken) {

        String userInfoUri = kakaoProviderProperties.userInfoUri();

        ResponseEntity<KakaoMemberInfoDto> entity = restClient.get()
            .uri(userInfoUri)
            .header("Authorization", "Bearer " + kakaoToken.getAccessToken())
            .header("Content-Type", "application/x-www-form-urlencoded")
            .retrieve()
            .toEntity(KakaoMemberInfoDto.class);

        return entity.getBody();
    }

    @Transactional
    public void saveMemberInfo(KakaoMemberInfoDto kakaoMemberInfoDto) {

        Long kakaoMemberId = kakaoMemberInfoDto.getId();

        Member member = memberRepository.findByKakaoMemberId(kakaoMemberId)
            .orElse(new Member(kakaoMemberId));
        member.updateProfile(kakaoMemberInfoDto.getProperties());

        memberRepository.save(member);
    }

    @Transactional
    public void saveKakaoToken(Long kakaoMemberId, KakaoTokenDto kakaoTokenDto) {

        Member member = memberRepository.findByKakaoMemberId(kakaoMemberId)
            .orElseThrow(IllegalArgumentException::new);
        KakaoToken kakaoToken = kakaoTokenDto.toEntity();

        kakaoTokenRepository.save(kakaoToken);

        member.updateKakaoToken(kakaoToken);
    }
}
