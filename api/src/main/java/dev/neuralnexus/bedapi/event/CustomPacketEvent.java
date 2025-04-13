package dev.neuralnexus.bedapi.event;

import dev.neuralnexus.bedapi.CustomPacketPayload;

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
