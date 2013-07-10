/*
Copyright 2013 Urban Airship and Contributors
*/
package com.urbanairship.sleighbells.notactuallytests;

import com.google.common.collect.ImmutableList;
import com.urbanairship.sleighbells.ua.api.PushSenderUaApiImpl;
import com.urbanairship.sleighbells.ua.model.*;

import java.util.List;

public class PushSenderEvaluator {

    private static final String MASTER_SECRET = "";
    private static final String APPKEY = "";

    private static final String APID = "";
    private static final String DEVICE_TOKEN = "";

    public static void main(String... args) {
        final App app = new App(APPKEY, MASTER_SECRET);
        final Message message = new Message("b", "in the drawer");
        final List<Device> bothRecipients = ImmutableList.of(
                new Device(APID, DeviceFamily.ANDROID),
                new Device(DEVICE_TOKEN, DeviceFamily.IOS));
        final PushRequest both = new PushRequest(bothRecipients, message);
        final PushSenderUaApiImpl sender = new PushSenderUaApiImpl(app);
        final List<SendRecord> records = sender.sendPushes(ImmutableList.of(both));
        for (SendRecord record : records) {
            System.out.print(record.getPushId());
        }
    }

}
