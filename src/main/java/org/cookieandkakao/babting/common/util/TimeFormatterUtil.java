package org.cookieandkakao.babting.common.util;

import java.time.format.DateTimeFormatter;

public class TimeFormatterUtil {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd'T'HH:mm:ssX");

}
