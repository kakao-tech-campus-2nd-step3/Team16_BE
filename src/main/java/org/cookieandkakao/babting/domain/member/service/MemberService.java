package org.cookieandkakao.babting.domain.member.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberInfoGetResponse;
import org.cookieandkakao.babting.domain.member.dto.KakaoTokenGetResponse;
import org.cookieandkakao.babting.domain.member.dto.MemberProfileGetResponse;
import org.cookieandkakao.babting.domain.member.entity.KakaoToken;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthService authService;

    public MemberService(MemberRepository memberRepository, AuthService authService) {
        this.memberRepository = memberRepository;
        this.authService = authService;
    }

    public MemberProfileGetResponse getMemberProfile(Long memberId) {
        Member member = findMember(memberId);
        return new MemberProfileGetResponse(memberId, member.getNickname(), member.getThumbnailImageUrl(),
            member.getProfileImageUrl());
    }

    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));
    }

    @Transactional
    public Long saveMemberInfoAndKakaoToken(
        KakaoMemberInfoGetResponse kakaoMemberInfoGetResponse,
        KakaoTokenGetResponse kakaoTokenGetResponse) {

        Long kakaoMemberId = kakaoMemberInfoGetResponse.id();

        Member member = memberRepository.findByKakaoMemberId(kakaoMemberId)
            .orElse(new Member(kakaoMemberId));
        member.updateProfile(kakaoMemberInfoGetResponse.properties());

        member = memberRepository.save(member);

        KakaoToken kakaoToken = kakaoTokenGetResponse.toEntity();

        member.updateKakaoToken(kakaoToken);

        return member.getMemberId();
    }

    @Transactional
    public void deleteMember(Long memberId) {
        String accessToken = getKakaoAccessToken(memberId);

        memberRepository.deleteById(memberId);
        // Todo: 해당 멤버와 관련된 entity들 삭제 로직 추가

        authService.unlinkMember(accessToken);

    }

    @Deprecated
    public KakaoToken getKakaoToken(Long memberId) {
        Member member = findMember(memberId);
        return member.getKakaoToken();
    }

    @Transactional
    public String getKakaoAccessToken(Long memberId) {
        Member member = findMember(memberId);
        KakaoToken kakaoToken = member.getKakaoToken();

        if (kakaoToken.getRefreshTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("카카오 refresh 토큰 만료");
        }
        else if (kakaoToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            KakaoTokenGetResponse kakaoTokenGetResponse = authService.refreshKakaoToken(
                kakaoToken.getRefreshToken());
            if (kakaoTokenGetResponse.refreshToken() == null) {
                kakaoToken.updateAccessToken(kakaoTokenGetResponse);
            }
            else {
                kakaoToken = kakaoTokenGetResponse.toEntity();
                member.updateKakaoToken(kakaoToken);
            }
        }

        return kakaoToken.getAccessToken();
    }

}
