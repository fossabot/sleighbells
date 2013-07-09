package com.urbanairship.sleighbells.notactuallytests.dummy;

import com.urbanairship.sleighbells.api.DeviceIdentifierSource;
import com.urbanairship.sleighbells.ua.model.Device;
import com.urbanairship.sleighbells.ua.model.DeviceFamily;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DummyIdSource implements DeviceIdentifierSource {

    private final int numToMake;

    public DummyIdSource(int numToMake) {
        this.numToMake = numToMake;
    }

    @Override
    public List<Device> getIdentifiers() {
        final ArrayList<Device> devices = new ArrayList<Device>(numToMake);
        for (int i = 0; i < numToMake; i++) {
            devices.add(new Device(UUID.randomUUID().toString(), DeviceFamily.ANDROID));
        }
        return devices;
    }
}
