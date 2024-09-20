package com.dnd.runus.global.constant;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum CacheType {
    WEATHER(Name.WEATHER, Duration.ofMinutes(10), 100),
    ;
    private final String cacheName;
    private final Duration expireAfterWrite;
    private final int maximumSize;

    CacheType(String cacheName, Duration expireAfterWrite, int maximumSize) {
        this.cacheName = cacheName;
        this.expireAfterWrite = expireAfterWrite;
        this.maximumSize = maximumSize;
    }

    public static class Name {
        public static final String WEATHER = "weather";
    }
}
