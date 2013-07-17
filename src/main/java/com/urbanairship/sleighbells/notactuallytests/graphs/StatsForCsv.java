package com.urbanairship.sleighbells.notactuallytests.graphs;

import com.google.common.base.Joiner;
import com.urbanairship.sleighbells.ua.api.BaseUaApiCommunication;
import com.urbanairship.sleighbells.ua.model.App;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class StatsForCsv {

    private static List<SendWeDid> loadSendsFromCsv(String filePath) {
        List<SendWeDid> messages = new ArrayList<SendWeDid>();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
            String sRead = fileReader.readLine();
            while (sRead != null && !sRead.isEmpty()) {
                final String[] split = sRead.split(",");
                messages.add(new SendWeDid(split[0], split[1], split[2]));
                sRead = fileReader.readLine();
            }
        } catch (Exception e) {
            throw new RuntimeException("error reading messages from file", e);
        }
        return messages;
    }

    public static void main(String... args) {

        final SystemConfiguration configuration = new SystemConfiguration();
        final String appkey = configuration.getString("ua.ab.appKey");
        final String masterSecret = configuration.getString("ua.ab.masterSecret");

        final App app = new App(appkey, masterSecret);

        final StatsHourlyChecker checker = new StatsHourlyChecker(app);

        final List<SendWeDid> sendsWeDid = loadSendsFromCsv(args[0]);

        Map<String, Map<Long, Integer>> sends = new TreeMap<String, Map<Long, Integer>>();
        Map<String, Map<Long, Integer>> opens = new TreeMap<String, Map<Long, Integer>>();

        for (SendWeDid sendWeDid : sendsWeDid) {
            if (!sends.containsKey(sendWeDid.messageName))
                sends.put(sendWeDid.messageName, new TreeMap<Long, Integer>());
            if (!opens.containsKey(sendWeDid.messageName))
                opens.put(sendWeDid.messageName, new TreeMap<Long, Integer>());
            Map<Long, Integer> pushSends = sends.get(sendWeDid.messageName);
            Map<Long, Integer> pushOpens = opens.get(sendWeDid.messageName);
            final StatsHourlyChecker.HourStats[] hourStats = checker.getHourStats(sendWeDid.pushId);
            for (StatsHourlyChecker.HourStats stat : hourStats) {
                final long millis = yyyyMMdd.parseDateTime(stat.getTime()).getMillis();
                final int extSends = pushSends.containsKey(millis) ? pushSends.get(millis) : 0;
                final int addlSends = extSends + stat.pushByPlatform.all.sends;
                pushSends.put(millis, addlSends);
                final int extOpens = pushOpens.containsKey(millis) ? pushOpens.get(millis) : 0;
                final int addlOpens = extOpens + stat.pushByPlatform.all.influencedOpens;
                pushOpens.put(millis, addlOpens);
            }
        }
        Set<Long> times = new TreeSet<Long>();
        for (Map<Long, Integer> dateTimeIntegerMap : sends.values()) {
            times.addAll(dateTimeIntegerMap.keySet());
        }
        List<Object> titles = new LinkedList<Object>();
        titles.add("series");
        titles.add("message");
        titles.addAll(times);
        System.out.println(Joiner.on(",").join(titles));
        printMapAsMatrix("sends", sends, times);
        printMapAsMatrix("opens", opens, times);
    }

    private static final DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static void printMapAsMatrix(String title, Map<String, Map<Long, Integer>> map, Set<Long> times) {
        for (String s : map.keySet()) {
            List<Object> womp = new LinkedList<Object>();
            womp.add(title);
            womp.add(s);
            for (Long time : times) {
                int i = map.get(s).containsKey(time) ? map.get(s).get(time) : 0;
                womp.add(i);
            }
            System.out.println(Joiner.on(",").join(womp));
        }
    }

    private static class SendWeDid {
        final String messageName;
        final String pushId;
        final DateTime sendTime;

        private SendWeDid(String messageName, String pushId, String sendTime) {
            this.messageName = messageName;
            this.pushId = pushId;
            this.sendTime = DateTime.parse(sendTime);
        }
    }

    private static class StatsHourlyChecker extends BaseUaApiCommunication {

        private static final String HOURLY_STATS_PATH = "api/reports/perpush/series/";

        public StatsHourlyChecker(App app) {
            super(app);
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class PlatformHourStats {
            int sends;
            int directOpens;
            int influencedOpens;

            public PlatformHourStats() {
            }

            public int getSends() {
                return sends;
            }

            public void setSends(int sends) {
                this.sends = sends;
            }

            public int getDirectOpens() {
                return directOpens;
            }

            public void setDirectOpens(int directOpens) {
                this.directOpens = directOpens;
            }

            public int getInfluencedOpens() {
                return influencedOpens;
            }

            public void setInfluencedOpens(int influencedOpens) {
                this.influencedOpens = influencedOpens;
            }

            @Override
            public String toString() {
                return "PlatformHourStats{" +
                        "sends=" + sends +
                        ", directOpens=" + directOpens +
                        ", influencedOpens=" + influencedOpens +
                        '}';
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Stats {
            public HourStats[] getCounts() {
                return counts;
            }

            public void setCounts(HourStats[] counts) {
                this.counts = counts;
            }

            private Stats() {

            }

            HourStats[] counts;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class PushByPlatform {
            PlatformHourStats ios;
            PlatformHourStats android;
            PlatformHourStats all;

            public PushByPlatform() {

            }

            public PlatformHourStats getIos() {
                return ios;
            }

            public void setIos(PlatformHourStats ios) {
                this.ios = ios;
            }

            public PlatformHourStats getAndroid() {
                return android;
            }

            public void setAndroid(PlatformHourStats android) {
                this.android = android;
            }

            public PlatformHourStats getAll() {
                return all;
            }

            public void setAll(PlatformHourStats all) {
                this.all = all;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class HourStats {
            PushByPlatform pushByPlatform;
            String time;

            public HourStats() {
            }

            public PushByPlatform getPushByPlatform() {
                return pushByPlatform;
            }

            public void setPushByPlatform(PushByPlatform pushByPlatform) {
                this.pushByPlatform = pushByPlatform;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            @Override
            public String toString() {
                return "HourStats{" +
                        "all=" + pushByPlatform.all +
                        ", time='" + time + '\'' +
                        '}';
            }
        }

        public HourStats[] getHourStats(String pushId) {
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String uri = HOURLY_STATS_PATH + pushId;
            HttpGet get = getGet(uri);
            boolean success = false;
            while (!success) {
                try {
                    String responseBody = CLIENT.execute(get, responseHandler);
                    final ObjectMapper mapper = new ObjectMapper();
                    final JsonNode rootNode = mapper.readValue(responseBody, JsonNode.class);
                    mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
                    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
                    final HourStats[] tokenListing = mapper.readValue(rootNode, Stats.class).getCounts();
                    success = true;
                    return tokenListing;
                } catch (Exception e) {
                    // log.warn("retrying stats read for id " + pushId, e);
                    System.out.println(e);
                }
            }
            return null;
        }

    }


}
