package org.cookieandkakao.babting.domain.member.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoMemberProfileDto {

    private String nickname;
    private String profileImage;
    private String thumbnailImage;

    public KakaoMemberProfileDto(String nickname, String profileImage, String thumbnailImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.thumbnailImage = thumbnailImage;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }
}
