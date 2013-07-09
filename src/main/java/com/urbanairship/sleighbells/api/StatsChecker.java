package com.urbanairship.sleighbells.api;

import com.urbanairship.sleighbells.ua.model.PushStatistics;

public interface StatsChecker {

    public PushStatistics getStatsForPush(String pushId);

}
