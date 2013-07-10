/*
Copyright 2013 Urban Airship and Contributors
*/
package com.urbanairship.sleighbells.notactuallytests.dummy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.urbanairship.sleighbells.agnostic.AbTestRunner;
import com.urbanairship.sleighbells.agnostic.MessageChooserBayesBetaBinomialImpl;
import com.urbanairship.sleighbells.ua.model.Message;

import java.util.concurrent.TimeUnit;

public class DummyRunner {

    public static void main(String... args) throws InterruptedException {
        Message first = new Message("a", "on the table");
        Message second = new Message("b", "in the drawer");
        final ImmutableMap<String, Double> successMap = ImmutableMap.of(first.identifier, .2, second.identifier, .39);
        final DummyStatsChecker checker = new DummyStatsChecker(successMap);
        final DummySender sender = new DummySender(checker);
        final DummyIdSource source = new DummyIdSource(10000);
        final MessageChooserBayesBetaBinomialImpl chooser = new MessageChooserBayesBetaBinomialImpl(1, 1);
        AbTestRunner.runExperiment(source, chooser, sender, checker, ImmutableList.of(first, second), 1, 1000, TimeUnit.MILLISECONDS);
    }

}
