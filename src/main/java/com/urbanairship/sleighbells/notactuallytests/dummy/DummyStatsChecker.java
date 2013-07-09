package com.urbanairship.sleighbells.notactuallytests.dummy;

import com.urbanairship.sleighbells.api.StatsChecker;
import com.urbanairship.sleighbells.ua.model.PushStatistics;

import java.util.HashMap;
import java.util.Map;

public class DummyStatsChecker implements StatsChecker {

    Map<String, Integer> pushIdToSuccessMap = new HashMap<String, Integer>();
    final Map<String, Double> messageIdToSuccessMap;

    public DummyStatsChecker(Map<String, Double> messageIdToSuccessMap) {
        this.messageIdToSuccessMap = messageIdToSuccessMap;
    }

    public void addSendForMessage(String messageId, String pushId) {
        final double successRate = messageIdToSuccessMap.get(messageId);
        pushIdToSuccessMap.put(pushId, Math.random() < successRate ? 1 : 0);
    }

    @Override
    public PushStatistics getStatsForPush(String pushId) {
        return new PushStatistics(1, pushIdToSuccessMap.get(pushId));
    }
}
