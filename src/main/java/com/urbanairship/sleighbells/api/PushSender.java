package com.urbanairship.sleighbells.api;

import com.urbanairship.sleighbells.ua.model.PushRequest;
import com.urbanairship.sleighbells.ua.model.SendRecord;

import java.util.List;

public interface PushSender {

    public List<SendRecord> sendPushes(List<PushRequest> requests);

}
