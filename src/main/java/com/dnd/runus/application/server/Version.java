package com.dnd.runus.application.server;

public record Version(int major, int minor, int patch) implements Comparable<Version> {
    public static Version parse(String version) {
        String[] split = version.split("\\.");
        if (split.length < 2 || split.length > 3) {
            throw new IllegalArgumentException("Invalid version format [" + version + "]");
        }
        int patch = split.length > 2 ? Integer.parseInt(split[2]) : 0;
        return new Version(Integer.parseInt(split[0]), Integer.parseInt(split[1]), patch);
    }

    @Override
    public int compareTo(Version o) {
        if (major != o.major) {
            return major - o.major;
        }
        if (minor != o.minor) {
            return minor - o.minor;
        }
        return patch - o.patch;
    }

    public boolean isOlderThan(Version version) {
        return compareTo(version) < 0;
    }
}
