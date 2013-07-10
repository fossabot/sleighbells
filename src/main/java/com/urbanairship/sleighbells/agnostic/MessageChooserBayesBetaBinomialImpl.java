/*
Copyright 2013 Urban Airship and Contributors
*/
package com.urbanairship.sleighbells.agnostic;

import com.google.common.collect.ImmutableList;
import com.urbanairship.sleighbells.api.MessageChooser;
import com.urbanairship.sleighbells.ua.model.Device;
import com.urbanairship.sleighbells.ua.model.Message;
import com.urbanairship.sleighbells.ua.model.PushRequest;
import com.urbanairship.sleighbells.ua.model.SendRecord;

import java.util.*;

public class MessageChooserBayesBetaBinomialImpl implements MessageChooser {

    private final int priorAlpha;
    private final int priorBeta;

    public MessageChooserBayesBetaBinomialImpl(int alpha, int beta) {
        this.priorAlpha = alpha;
        this.priorBeta = beta;
    }

    @Override
    public List<PushRequest> chooseNPushes(Collection<SendRecord> history, List<Device> targets, List<Message> messages) {
        // collate the results of the pushes sent so far by grouping sends with their respective messages
        Map<Message, BetaDistribution> betaMap = new HashMap<Message, BetaDistribution>();
        // initialize each message's distribution with the prior.
        for (Message message : messages) {
            betaMap.put(message, new BetaDistribution(priorAlpha, priorBeta));
        }
        // observe the results of the pushes so far
        for (SendRecord sendRecord : history) {
            final BetaDistribution beta = betaMap.get(sendRecord.getPushRequest().getMessage());
            final int additionalAlpha = sendRecord.getStatistics().influence;
            final int additionalBeta = Math.max(0, sendRecord.getStatistics().sends - additionalAlpha);
            beta.addBeta(additionalBeta);
            beta.addAlpha(additionalAlpha);
        }
        // build a list of push requests.
        List<PushRequest> requests = new ArrayList<PushRequest>(targets.size());
        for (Device device : targets) {
            final Message message = chooseAMessage(betaMap);
            requests.add(new PushRequest(ImmutableList.of(device), message));
        }
        return requests;
    }

    // The Thompson technique for choosing a message is to sample the parameters of the likelihood
    // distribution for each message, then choose the one with the maximum likelihood. Here, the parameter
    // is the likelihood, so we just compare those.
    private static Message chooseAMessage(Map<Message, BetaDistribution> betaMap) {
        double max = Double.NEGATIVE_INFINITY;
        Message r = null;
        for (Map.Entry<Message, BetaDistribution> entry : betaMap.entrySet()) {
            // draw the sample
            final double sample = entry.getValue().sample();
            // update the maximum likelihood & associated message
            if (sample > max) {
                r = entry.getKey();
                max = sample;
            }
        }
        return r;
    }

    private static class BetaDistribution {
        private int alpha;
        private int beta;

        public BetaDistribution(int priorA, int priorB) {
            this.alpha = priorA;
            this.beta = priorB;
        }

        public void addBeta(double b) {
            this.beta += b;
        }

        public void addAlpha(double a) {
            this.alpha += a;
        }

        public double sample() {
            return betaSample(alpha, beta);
        }

        private static double betaSample(int alpha, int beta) {
            final double a = gammaSample(alpha);
            final double b = gammaSample(beta);
            return a / (a + b);
        }

        private static double gammaSample(int n) {
            double d = 0;
            for (int i = 0; i < n; i++) {
                d -= Math.log(Math.random());
            }
            return d;
        }
    }

}
