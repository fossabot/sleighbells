package com.urbanairship.sleighbells.notactuallytests;

import com.google.common.collect.ImmutableList;
import com.urbanairship.sleighbells.agnostic.AbTestRunner;
import com.urbanairship.sleighbells.ua.api.StatsCheckerUaApiImpl;
import com.urbanairship.sleighbells.ua.model.App;
import com.urbanairship.sleighbells.ua.model.PushRequest;
import com.urbanairship.sleighbells.ua.model.SendRecord;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class StatisticsEvaluator {

    private static final String pushId = "";

    private static final String MASTER_SECRET = "";
    private static final String APPKEY = "";

    public static void main(String... args) {
        final App app = new App(APPKEY, MASTER_SECRET);
        final PushRequest nullRequest = new PushRequest(null, null);
        final SendRecord sendRecord = new SendRecord(pushId, nullRequest, DateTime.now(DateTimeZone.UTC));
        final StatsCheckerUaApiImpl statsCheckerUaApi = new StatsCheckerUaApiImpl(app);
        AbTestRunner.updateAndPrintStatistics(statsCheckerUaApi, ImmutableList.of(sendRecord));
        System.out.println(sendRecord.getStatistics().sends + " " + sendRecord.getStatistics().influence);
        statsCheckerUaApi.shutdown();
    }

}
