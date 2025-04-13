package dev.neuralnexus.bedapi.event;

import dev.neuralnexus.taterapi.event.Event;

public class NetworkEvent implements Event {
    private final Object connection;

    public NetworkEvent(Object connection) {
        this.connection = connection;
    }

    public Object connection() {
        return this.connection;
    }
}
