package com.dnd.runus.application.server;

import org.springframework.stereotype.Service;

@Service
public class ServerVersionService {
    public boolean isUpdateRequired(Version version, DeviceType client) {
        return version.isOlderThan(client.getMinSupportedVersion());
    }
}
