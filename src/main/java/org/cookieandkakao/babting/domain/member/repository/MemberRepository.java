package org.cookieandkakao.babting.domain.member.repository;

import java.util.Optional;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByKakaoMemberId(Long kakaoMemberId);
}
