package dev.neuralnexus.bedapi.event;

import dev.neuralnexus.bedapi.CustomPacketPayload;
import dev.neuralnexus.taterapi.event.Event;

public class S2CCustomPacketEvent extends CustomPacketEvent {
    private final Object server;

    public S2CCustomPacketEvent(CustomPacketPayload payload, Object connection, Object server) {
        super(payload, connection);
        this.server = server;
    }

    public Object server() {
        return this.server;
    }
}
