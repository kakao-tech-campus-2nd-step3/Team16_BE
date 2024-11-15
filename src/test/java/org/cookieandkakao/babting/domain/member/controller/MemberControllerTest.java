package org.cookieandkakao.babting.domain.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.cookieandkakao.babting.domain.member.dto.MemberProfileGetResponse;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    @Test
    void 회원_프로필_조회_성공() {
        // given
        Long memberId = 1L;
        MemberProfileGetResponse memberProfileGetResponse =
            new MemberProfileGetResponse(memberId, "nickname", "thumnail.jpg", "profile.jpg");

        given(memberService.getMemberProfile(any())).willReturn(memberProfileGetResponse);

        // when
        var response = memberController.getMemberProfile(memberId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isEqualTo(memberProfileGetResponse);
        assertThat(response.getBody().getMessage()).isEqualTo("프로필 조회 성공");
    }

    @Test
    void 회원_탈퇴_성공() {
        // given
        Long memberId = 1L;

        // when
        var response = memberController.deleteMember(memberId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("회원 탈퇴 성공");
    }

}