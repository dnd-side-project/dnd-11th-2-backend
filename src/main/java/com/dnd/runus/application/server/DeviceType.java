package com.dnd.runus.application.server;

import lombok.Getter;

@Getter
public enum DeviceType {
    IOS(new Version(1, 0, 0)),
    ;
    private final Version minSupportedVersion;

    DeviceType(Version version) {
        this.minSupportedVersion = version;
    }
}
