package org.cookieandkakao.babting.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.cookieandkakao.babting.domain.member.dto.KakaoMemberProfileGetResponse;

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

    @OneToOne
    @JoinColumn(name = "kako_token_id")
    private KakaoToken kakaoToken;

    protected Member() {
    }

    public Member(Long kakaoMemberId) {
        this.kakaoMemberId = kakaoMemberId;
    }

    public void updateProfile(KakaoMemberProfileGetResponse profileDto) {
        this.nickname = profileDto.nickname();
        this.thumbnailImageUrl = profileDto.thumbnailImage();
        this.profileImageUrl = profileDto.profileImage();
    }

    public void updateKakaoToken(KakaoToken kakaoToken) {
        this.kakaoToken = kakaoToken;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public KakaoToken getKakaoToken() {
        return kakaoToken;
    }
}
