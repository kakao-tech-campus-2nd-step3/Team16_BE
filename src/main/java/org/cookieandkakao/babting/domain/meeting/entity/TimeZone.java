package org.cookieandkakao.babting.domain.meeting.entity;

public enum TimeZone {
    SEOUL("Asia/Seoul");

    private String area;

    TimeZone(String area) {
        this.area = area;
    }

    public String getArea() {
        return area;
    }
}
