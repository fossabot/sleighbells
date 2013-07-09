package com.urbanairship.sleighbells.agnostic;

import com.urbanairship.sleighbells.api.DeviceIdentifierSource;
import com.urbanairship.sleighbells.ua.model.Device;
import com.urbanairship.sleighbells.ua.model.DeviceFamily;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class DeviceIdentifierSourceFlatFileImpl implements DeviceIdentifierSource {

    private final String filePath;

    public DeviceIdentifierSourceFlatFileImpl(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Device> getIdentifiers() {
        List<Device> devices = new ArrayList<Device>();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
            String sRead = fileReader.readLine();
            while (sRead != null && !sRead.isEmpty()) {
                final String[] split = sRead.split("\\s");
                String name = split[0];
                final DeviceFamily deviceFamily = DeviceFamily.fromString(split[1]);
                if (deviceFamily == DeviceFamily.IOS) {
                    name = name.toUpperCase();
                }
                devices.add(new Device(name, deviceFamily));
                sRead = fileReader.readLine();
            }
        } catch (Exception e) {
            throw new RuntimeException("error reading ids from file", e);
        }
        return devices;
    }

}
