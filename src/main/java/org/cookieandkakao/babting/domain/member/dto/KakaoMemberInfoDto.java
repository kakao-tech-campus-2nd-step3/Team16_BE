package org.cookieandkakao.babting.domain.member.dto;

public class KakaoMemberInfoDto {

    private Long id;
    private KakaoMemberProfileDto properties;

    public KakaoMemberInfoDto(Long id, KakaoMemberProfileDto properties) {
        this.id = id;
        this.properties = properties;
    }

    public Long getId() {
        return id;
    }

    public KakaoMemberProfileDto getProperties() {
        return properties;
    }
}
