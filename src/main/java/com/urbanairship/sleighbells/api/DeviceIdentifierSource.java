/*
Copyright 2013 Urban Airship and Contributors
*/
package com.urbanairship.sleighbells.api;

import com.urbanairship.sleighbells.ua.model.Device;

import java.util.List;

public interface DeviceIdentifierSource {

    public List<Device> getIdentifiers();

}
