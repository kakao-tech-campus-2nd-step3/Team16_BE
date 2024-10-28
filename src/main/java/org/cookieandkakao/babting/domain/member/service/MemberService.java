package org.cookieandkakao.babting.domain.member.service;

import jakarta.transaction.Transactional;
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
    public void deleteMember(Long memberId) {
        String accessToken = getKakaoToken(memberId).getAccessToken();
        authService.unlinkMember(accessToken);

        memberRepository.deleteById(memberId);
        // Todo: 해당 멤버와 관련된 entity들 삭제 로직 추가
    }

    public KakaoToken getKakaoToken(Long memberId) {
        Member member = findMember(memberId);
        return member.getKakaoToken();
    }
}
