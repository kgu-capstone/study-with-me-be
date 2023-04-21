package com.kgu.studywithme.global.slack;

public interface SlackMetadata {
    String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    String XFF_HEADER = "X-FORWARDED-FOR";
    String LOG_COLOR = "FF0000";
    String TITLE_REQUEST_IP = "[Request IP]";
    String TITLE_REQUEST_URL = "[Request URL]";
    String TITLE_ERROR_MESSAGE = "[Error Message]";
}
