/*
Copyright 2013 Urban Airship and Contributors
*/
package com.urbanairship.sleighbells.agnostic;

import com.google.common.base.Joiner;
import com.urbanairship.sleighbells.api.DeviceIdentifierSource;
import com.urbanairship.sleighbells.api.MessageChooser;
import com.urbanairship.sleighbells.api.PushSender;
import com.urbanairship.sleighbells.api.StatsChecker;
import com.urbanairship.sleighbells.ua.api.DeviceIdentifierSourceUaApiImpl;
import com.urbanairship.sleighbells.ua.api.PushSenderUaApiImpl;
import com.urbanairship.sleighbells.ua.api.StatsCheckerUaApiImpl;
import com.urbanairship.sleighbells.ua.model.*;
import org.apache.commons.configuration.SystemConfiguration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AbTestRunner {

    public static void main(String... args) throws InterruptedException {

        final SystemConfiguration configuration = new SystemConfiguration();
        final String messagesFile = configuration.getString("ua.ab.messagesFile");
        final String appkey = configuration.getString("ua.ab.appKey");
        final String masterSecret = configuration.getString("ua.ab.masterSecret");
        final int batchSize = configuration.getInt("ua.ab.batchSize");
        final int totalHours = configuration.getInt("ua.ab.hoursToUse");
        final String devicesSource = configuration.getString("ua.ab.devicesSource");

        final App app = new App(appkey, masterSecret);
        final StatsChecker checker = new StatsCheckerUaApiImpl(app);
        final PushSender sender = new PushSenderUaApiImpl(app);
        final List<Message> messages = parseMessagesFile(messagesFile);
        final MessageChooserBayesBetaBinomialImpl chooser = new MessageChooserBayesBetaBinomialImpl(1, 1);

        final DeviceIdentifierSource source;
        if (devicesSource.equals("API")) {
            source = new DeviceIdentifierSourceUaApiImpl(app);
        } else {
            source = new DeviceIdentifierSourceFlatFileImpl(devicesSource);
        }

        // send the pushes.
        final List<SendRecord> sends = runExperiment(source, chooser, sender, checker, messages, batchSize, totalHours, TimeUnit.HOURS);

        // print updated statistics every minute until someone kills us.
        while (true) {
            updateAndPrintStatistics(checker, sends);
            Thread.sleep(TimeUnit.MINUTES.toMillis(1));
        }
    }

    public static List<SendRecord> runExperiment(DeviceIdentifierSource source, MessageChooser chooser,
                                                 PushSender sender, StatsChecker checker,
                                                 List<Message> messages, int batchSize,
                                                 long totalTime, TimeUnit unit) throws InterruptedException {

        List<Device> allDevices = source.getIdentifiers();
        Collections.shuffle(allDevices);

        long totalMillis = unit.toMillis(totalTime);
        long millisBetween = (long) Math.floor(totalMillis / Math.ceil(allDevices.size() / (double) batchSize));

        List<SendRecord> sends = new LinkedList<SendRecord>();
        int pushesStart = 0;
        while (pushesStart < allDevices.size()) {
            // hit the reports API to update send and open counts.
            updateAndPrintStatistics(checker, sends);

            // choose a message for each of the next batchSize users.
            final List<PushRequest> requests =
                    chooser.chooseNPushes(
                            sends,
                            allDevices.subList(
                                    pushesStart,
                                    Math.min(pushesStart + batchSize, allDevices.size())),
                            messages);

            // send those pushes.
            final List<SendRecord> records = sender.sendPushes(requests);
            sends.addAll(records);

            // move the user-pointer
            pushesStart += batchSize;

            // wait for the next batch release.
            Thread.sleep(millisBetween);
        }

        return sends;
    }

    public static void updateAndPrintStatistics(StatsChecker checker, List<SendRecord> records) {
        for (SendRecord record : records) {
            record.setStatistics(checker.getStatsForPush(record.getPushId()));
        }
        printStatistics(records);
    }

    public static void printStatistics(List<SendRecord> sends) {
        Map<Message, List<SendRecord>> aggregates = new HashMap<Message, List<SendRecord>>();
        for (SendRecord send : sends) {
            if (!aggregates.containsKey(send.getPushRequest().getMessage()))
                aggregates.put(send.getPushRequest().getMessage(), new LinkedList<SendRecord>());
            aggregates.get(send.getPushRequest().getMessage()).add(send);
        }
        for (Message message : aggregates.keySet()) {
            int totalSends = 0;
            int totalInfl = 0;
            for (SendRecord sendRecord : aggregates.get(message)) {
                totalSends += sendRecord.getStatistics().sends;
                totalInfl += sendRecord.getStatistics().influence;
            }

            String stats = "Sent message with id <" + message.identifier + "> " +
                    totalSends + " times with " +
                    totalInfl + " influence for a rate of " + (totalInfl / (double) totalSends);
            System.out.println(stats);
        }
    }

    public static List<Message> parseMessagesFile(String filePath) {
        List<Message> messages = new ArrayList<Message>();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
            String sRead = fileReader.readLine();
            String title = null;
            List<String> linesOfMessage = new LinkedList<String>();
            while (sRead != null && !sRead.isEmpty()) {
                if (sRead.toLowerCase().startsWith("title:")) {
                    if (title != null) {
                        messages.add(new Message(title, Joiner.on("\n").join(linesOfMessage)));
                        linesOfMessage.clear();
                    }
                    title = sRead.substring(6).trim();
                } else {
                    linesOfMessage.add(sRead);
                }
                sRead = fileReader.readLine();
            }
            if (title != null) {
                messages.add(new Message(title, Joiner.on("\n").join(linesOfMessage)));
                linesOfMessage.clear();
            }
        } catch (Exception e) {
            throw new RuntimeException("error reading messages from file", e);
        }
        return messages;
    }

}
