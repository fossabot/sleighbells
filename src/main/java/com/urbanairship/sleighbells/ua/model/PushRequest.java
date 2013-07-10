/*
Copyright 2013 Urban Airship and Contributors
*/
package com.urbanairship.sleighbells.ua.model;

import com.google.common.collect.ImmutableList;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

public class PushRequest {

    private final List<Device> recipients;
    private final Message message;

    public PushRequest(List<Device> recipients, Message message) {
        this.recipients = ImmutableList.copyOf(recipients);
        this.message = message;
    }

    public List<Device> getRecipients() {
        return recipients;
    }

    public Message getMessage() {
        return message;
    }

    public String toJson() throws IOException {

        List<String> apids = new LinkedList<String>();
        List<String> deviceTokens = new LinkedList<String>();
        for (Device recipient : recipients) {
            if (recipient.getDeviceFamily().equals(DeviceFamily.IOS))
                deviceTokens.add(recipient.getPushableAddress());
            else apids.add(recipient.getPushableAddress());
        }
        Msg android = null;
        if (apids.size() > 0) {
            android = new Msg(message.message);
        } else {
            apids = null;
        }
        Msg aps = null;
        if (deviceTokens.size() > 0) {
            aps = new Msg(message.message);
        } else {
            deviceTokens = null;
        }

        StringWriter stringWriter = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        mapper.writeValue(stringWriter, new SendableMessage(apids, deviceTokens, android, aps));

        return stringWriter.toString();
    }

    @JsonIgnoreProperties
    private class SendableMessage {
        List<String> apids;
        List<String> deviceTokens;
        Msg android;
        Msg aps;

        public List<String> getApids() {
            return apids;
        }

        public void setApids(List<String> apids) {
            this.apids = apids;
        }

        public List<String> getDeviceTokens() {
            return deviceTokens;
        }

        public void setDeviceTokens(List<String> deviceTokens) {
            this.deviceTokens = deviceTokens;
        }

        public Msg getAndroid() {
            return android;
        }

        public void setAndroid(Msg android) {
            this.android = android;
        }

        public Msg getAps() {
            return aps;
        }

        public void setAps(Msg aps) {
            this.aps = aps;
        }

        private SendableMessage(List<String> apids, List<String> deviceTokens, Msg android, Msg aps) {
            this.apids = apids;
            this.deviceTokens = deviceTokens;
            this.android = android;
            this.aps = aps;
        }
    }

    @JsonIgnoreProperties
    private class Msg {
        String alert;

        private Msg(String alert) {
            this.alert = alert;
        }

        public String getAlert() {
            return alert;
        }

        public void setAlert(String alert) {
            this.alert = alert;
        }
    }

}
