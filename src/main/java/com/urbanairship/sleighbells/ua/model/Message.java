/*
Copyright 2013 Urban Airship and Contributors
*/
package com.urbanairship.sleighbells.ua.model;

public class Message {

    public final String identifier;
    public final String message;

    public Message(String identifier, String message) {
        this.identifier = identifier;
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (!identifier.equals(message1.identifier)) return false;
        if (!message.equals(message1.message)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + message.hashCode();
        return result;
    }
}
