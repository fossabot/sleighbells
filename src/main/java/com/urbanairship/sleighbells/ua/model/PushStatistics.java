package com.urbanairship.sleighbells.ua.model;

public class PushStatistics {

    public final int sends;
    public final int influence;

    public PushStatistics(int sends, int influence) {
        this.sends = sends;
        this.influence = influence;
    }
}
