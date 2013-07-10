/*
Copyright 2013 Urban Airship and Contributors
*/
package com.urbanairship.sleighbells.notactuallytests.dummy;

import com.urbanairship.sleighbells.api.PushSender;
import com.urbanairship.sleighbells.ua.model.PushRequest;
import com.urbanairship.sleighbells.ua.model.SendRecord;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DummySender implements PushSender {

    final DummyStatsChecker statsChecker;

    public DummySender(DummyStatsChecker statsChecker) {
        this.statsChecker = statsChecker;
    }

    @Override
    public List<SendRecord> sendPushes(List<PushRequest> requests) {
        List<SendRecord> records = new ArrayList<SendRecord>(requests.size());
        for (PushRequest request : requests) {
            final String pushId = UUID.randomUUID().toString();
            records.add(new SendRecord(pushId, request, DateTime.now(DateTimeZone.UTC)));
            statsChecker.addSendForMessage(request.getMessage().identifier, pushId);
        }
        return records;
    }
}
