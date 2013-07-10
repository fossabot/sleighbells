/*
Copyright 2013 Urban Airship and Contributors
*/
package com.urbanairship.sleighbells.ua.api;

import com.google.common.base.Joiner;
import com.urbanairship.sleighbells.api.PushSender;
import com.urbanairship.sleighbells.ua.model.App;
import com.urbanairship.sleighbells.ua.model.PushRequest;
import com.urbanairship.sleighbells.ua.model.SendRecord;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

public class PushSenderUaApiImpl extends BaseUaApiCommunication implements PushSender {

    private static final Logger log = LogManager.getLogger(PushSenderUaApiImpl.class);

    private static final String PUSH_PATH = "api/push/";

    public PushSenderUaApiImpl(App app) {
        super(app);
    }

    @Override
    public List<SendRecord> sendPushes(List<PushRequest> requests) {
        List<SendRecord> records = new ArrayList<SendRecord>(requests.size());
        for (PushRequest request : requests) {
            final HttpPost post = getPost(PUSH_PATH);
            post.setHeader("Content-Type", "application/json");
            try {

                post.setEntity(new StringEntity(request.toJson()));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = CLIENT.execute(post, responseHandler);
                final JsonNode rootNode = new ObjectMapper().readValue(responseBody, JsonNode.class);
                final String uuidText = rootNode.get("push_id").asText();
                final DateTime sendTime = DateTime.now(DateTimeZone.UTC);
                records.add(new SendRecord(uuidText, request, sendTime));

                log.debug(
                        "sent message " + request.getMessage().identifier +
                                " got id " + uuidText +
                                " at " + sendTime +
                                " to " + Joiner.on(",").join(request.getRecipients())
                );

            } catch (Exception e) {
                throw new RuntimeException("error sending pushes", e);
            }
        }
        return records;
    }
}
