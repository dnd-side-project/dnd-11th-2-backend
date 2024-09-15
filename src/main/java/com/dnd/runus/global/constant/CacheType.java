package com.dnd.runus.global.constant;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum CacheType {
    ;
    private final String cacheName;
    private final Duration expireAfterWrite;
    private final int maximumSize;

    CacheType(String cacheName, Duration expireAfterWrite, int maximumSize) {
        this.cacheName = cacheName;
        this.expireAfterWrite = expireAfterWrite;
        this.maximumSize = maximumSize;
    }
}
