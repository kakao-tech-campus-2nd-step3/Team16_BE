package org.cookieandkakao.babting.domain.member;

import jakarta.servlet.http.HttpServletRequest;
import org.cookieandkakao.babting.domain.member.dto.KakaoTokenDto;
import org.cookieandkakao.babting.domain.member.dto.MemberDto;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.cookieandkakao.babting.domain.member.util.JwtUtil;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public LoginMemberArgumentResolver(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        String bearerToken = request.getHeader("Authorization");
        String accessToken = "";

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            accessToken = bearerToken.substring(7);
        }

        Long userId = Long.parseLong(jwtUtil.parseClaims(accessToken).getSubject());

        Member loginMember = memberRepository.findById(userId)
            .orElseThrow(IllegalArgumentException::new);

        return new MemberDto(loginMember.getMemberId(), loginMember.getNickname(),
            loginMember.getThumbnailImageUrl(), loginMember.getProfileImageUrl(),
            new KakaoTokenDto(loginMember.getKakaoToken()));
    }
}
