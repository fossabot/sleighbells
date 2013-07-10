/*
Copyright 2013 Urban Airship and Contributors
*/
package com.urbanairship.sleighbells.notactuallytests;

import com.google.common.collect.ImmutableList;
import com.urbanairship.sleighbells.ua.model.Device;
import com.urbanairship.sleighbells.ua.model.DeviceFamily;
import com.urbanairship.sleighbells.ua.model.Message;
import com.urbanairship.sleighbells.ua.model.PushRequest;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PushRequestEvaluator {

    private static final String APID = "";
    private static final String DEVICE_TOKEN = "";

    public static void main(String... args) throws IOException {

        final List<Device> androindOnlyRecipients = ImmutableList.of(new Device(APID, DeviceFamily.ANDROID));
        final Message message = new Message("a", "on the table");
        final PushRequest androidOnly = new PushRequest(androindOnlyRecipients, message);
        System.out.println(androidOnly.toJson());

        final List<Device> iosOnlyRecipients = ImmutableList.of(new Device(DEVICE_TOKEN, DeviceFamily.IOS));
        final PushRequest iosOnly = new PushRequest(iosOnlyRecipients, message);
        System.out.println(iosOnly.toJson());

        final List<Device> bothRecipients = new LinkedList<Device>();
        bothRecipients.addAll(androindOnlyRecipients);
        bothRecipients.addAll(iosOnlyRecipients);
        final PushRequest both = new PushRequest(bothRecipients, message);
        System.out.println(both.toJson());

    }

}
