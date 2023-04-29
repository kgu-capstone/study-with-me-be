package com.kgu.studywithme.global.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
public class TimeZoneConfiguration {
    private static final String KOREA_TIMEZONE = "Asia/Seoul";

    @PostConstruct
    public void initTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(KOREA_TIMEZONE));
    }
}
