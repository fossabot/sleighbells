/*
Copyright 2013 Urban Airship and Contributors
*/
package com.urbanairship.sleighbells.ua.model;

public class Device {
    public String getPushableAddress() {
        return pushableAddress;
    }

    public DeviceFamily getDeviceFamily() {
        return deviceFamily;
    }

    private final String pushableAddress;
    private final DeviceFamily deviceFamily;

    public Device(String pushableAddress, DeviceFamily deviceFamily) {
        this.pushableAddress = pushableAddress;
        this.deviceFamily = deviceFamily;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        if (deviceFamily != device.deviceFamily) return false;
        if (!pushableAddress.equals(device.pushableAddress)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pushableAddress.hashCode();
        result = 31 * result + deviceFamily.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "(" + pushableAddress + "," + deviceFamily + ")";
    }
}
