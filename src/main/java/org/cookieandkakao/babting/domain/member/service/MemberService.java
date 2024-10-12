package org.cookieandkakao.babting.domain.member.service;

import org.cookieandkakao.babting.domain.member.dto.MemberProfileGetResponse;
import org.cookieandkakao.babting.domain.member.entity.KakaoToken;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
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

    public KakaoToken getKakaoToken(Long memberId) {
        Member member = findMember(memberId);
        return member.getKakaoToken();
    }
}
