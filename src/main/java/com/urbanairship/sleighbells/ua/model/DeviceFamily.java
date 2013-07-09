package com.urbanairship.sleighbells.ua.model;

public enum DeviceFamily {

    ANDROID, IOS;

    public static DeviceFamily fromString(String s) {
        return valueOf(s.toUpperCase());
    }

}
