package com.urbanairship.sleighbells.api;

import com.urbanairship.sleighbells.ua.model.Device;
import com.urbanairship.sleighbells.ua.model.Message;
import com.urbanairship.sleighbells.ua.model.PushRequest;
import com.urbanairship.sleighbells.ua.model.SendRecord;

import java.util.Collection;
import java.util.List;

public interface MessageChooser {

    public List<PushRequest> chooseNPushes(Collection<SendRecord> history,
                                           List<Device> targets,
                                           List<Message> messages);

}
