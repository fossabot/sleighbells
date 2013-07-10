/*
Copyright 2013 Urban Airship and Contributors
*/
package com.urbanairship.sleighbells.notactuallytests;

import com.urbanairship.sleighbells.agnostic.DeviceIdentifierSourceFlatFileImpl;
import com.urbanairship.sleighbells.ua.api.DeviceIdentifierSourceUaApiImpl;
import com.urbanairship.sleighbells.ua.model.App;
import com.urbanairship.sleighbells.ua.model.Device;

import java.util.Collection;

public class DeviceEvaluator {

    private static final String MASTER_SECRET = "";
    private static final String APPKEY = "";

    public static void main(String... args) {
        Collection<Device> identifiers = new DeviceIdentifierSourceFlatFileImpl("resources/devices.dat").getIdentifiers();
        for (Device identifier : identifiers) {
            System.out.println(identifier.getPushableAddress() + " " + identifier.getDeviceFamily());
        }

        final App app = new App(APPKEY, MASTER_SECRET);
        identifiers = new DeviceIdentifierSourceUaApiImpl(app).getIdentifiers();
        for (Device identifier : identifiers) {
            System.out.println(identifier.getPushableAddress() + " " + identifier.getDeviceFamily());
        }
    }

}
