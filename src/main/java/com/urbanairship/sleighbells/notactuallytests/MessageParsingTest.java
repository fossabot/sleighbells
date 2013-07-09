package com.urbanairship.sleighbells.notactuallytests;

import com.urbanairship.sleighbells.agnostic.AbTestRunner;
import com.urbanairship.sleighbells.ua.model.Message;

import java.util.List;

public class MessageParsingTest {

    public static void main(String... args) {
        final List<Message> messages = AbTestRunner.parseMessagesFile("resources/sleighbells-msg.txt");
        for (Message message : messages) {
            System.out.println(message.identifier);
            System.out.println(message.message);
        }
    }

}
