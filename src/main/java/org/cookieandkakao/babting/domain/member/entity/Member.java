package org.cookieandkakao.babting.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberProfileDto;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private Long kakaoMemberId;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String thumbnailImageUrl;

    @Column
    private String profileImageUrl;

    @Column
    private String accessToken;

    protected Member() {}

    public Member(Long kakaoMemberId) {
        this.kakaoMemberId = kakaoMemberId;
    }

    public void updateProfile(KakaoMemberProfileDto profileDto) {
        this.nickname = profileDto.getNickname();
        this.thumbnailImageUrl = profileDto.getThumbnailImage();
        this.profileImageUrl = profileDto.getProfileImage();
    }
}
