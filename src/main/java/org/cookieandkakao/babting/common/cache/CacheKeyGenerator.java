package org.cookieandkakao.babting.common.cache;

public class CacheKeyGenerator {

    private static final String EVENT_LIST = "eventListCache::";
    private static final String EVENT_DETAIL = "eventDetailCache::";

    // 일정 목록 캐시 키
    public static String generateEventListKey(Long memberId, String from, String to) {
        return EVENT_LIST + memberId + from + to;
    }

    // 일정 상세 정보 캐시 키
    public static String generateEventDetailKey(String eventId) {
        return EVENT_DETAIL + eventId;
    }

    // 특정 memberId를 포함한 캐시 키 패턴 생성
    public static String generateEventListPattern(Long memberId) {
        return EVENT_LIST + memberId + "*";
    }
}
