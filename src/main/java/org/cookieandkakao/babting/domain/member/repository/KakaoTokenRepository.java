package org.cookieandkakao.babting.domain.member.repository;

import org.cookieandkakao.babting.domain.member.entity.KakaoToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoTokenRepository extends JpaRepository<KakaoToken, Long> {

}
