package com.urbanairship.sleighbells.ua.model;

import org.joda.time.DateTime;

public class SendRecord {

    private final String pushId;
    private final PushRequest pushRequest;
    private PushStatistics statistics;
    private final DateTime sendTime;

    public SendRecord(String pushId, PushRequest pushRequest, DateTime sendTime) {
        this.pushRequest = pushRequest;
        this.pushId = pushId;
        this.sendTime = sendTime;
    }

    public void setStatistics(PushStatistics statistics) {
        this.statistics = statistics;
    }

    public PushStatistics getStatistics() {
        return statistics;
    }

    public PushRequest getPushRequest() {
        return pushRequest;
    }

    public String getPushId() {
        return pushId;
    }

    public DateTime getSendTime() {
        return sendTime;
    }
}
