package dev.neuralnexus.bedapi.event;

import dev.neuralnexus.bedapi.CustomPacketPayload;
import dev.neuralnexus.taterapi.event.Event;

public class CustomPacketEvent extends NetworkEvent {
    private final CustomPacketPayload payload;

    public CustomPacketEvent(CustomPacketPayload payload, Object connection) {
        super(connection);
        this.payload = payload;
    }

    public CustomPacketPayload payload() {
        return this.payload;
    }
}
