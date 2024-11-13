package org.cookieandkakao.babting.common.cache;

public class CacheKeyGenerator {

    private static final String EVENT_LIST = "eventListCache::";
    private static final String EVENT_DETAIL = "eventDetailCache::";

    public static String generateEventListKey(Long memberId, String from, String to) {
        return EVENT_LIST + memberId + from + to;
    }

    public static String generateEventDetailKey(String eventId) {
        return EVENT_DETAIL + eventId;
    }

    public static String generateEventListPattern(Long memberId) {
        return EVENT_LIST + memberId + "*";
    }
}
