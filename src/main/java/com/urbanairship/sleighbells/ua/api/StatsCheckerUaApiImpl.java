package com.urbanairship.sleighbells.ua.api;

import com.urbanairship.sleighbells.api.StatsChecker;
import com.urbanairship.sleighbells.ua.model.App;
import com.urbanairship.sleighbells.ua.model.PushStatistics;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class StatsCheckerUaApiImpl extends BaseUaApiCommunication implements StatsChecker {


    private static final Logger log = LogManager.getLogger(StatsCheckerUaApiImpl.class);

    private static final String SINGLE_PUSH_STATS_PATH = "api/reports/perpush/detail/";

    public StatsCheckerUaApiImpl(App app) {
        super(app);
    }

    @Override
    public PushStatistics getStatsForPush(String pushId) {

        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        final String uri = SINGLE_PUSH_STATS_PATH + pushId;
        HttpGet get = getGet(uri);
        boolean success = false;
        while (!success) {
            try {
                String responseBody = CLIENT.execute(get, responseHandler);
                final JsonNode rootNode = new ObjectMapper().readValue(responseBody, JsonNode.class);
                final int opens = rootNode.get("influenced_opens").asInt();
                final int sends = rootNode.get("sends").asInt();
                success = true;
                return new PushStatistics(sends, opens);
            } catch (Exception e) {
                log.warn("retrying stats read for id " + pushId, e);
            }
        }
        return null;
    }

}
