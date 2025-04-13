package dev.neuralnexus.bedapi.event;

import dev.neuralnexus.bedapi.CustomPacketPayload;

public class C2SCustomPacketEvent extends CustomPacketEvent {
    private final Object player;

    public C2SCustomPacketEvent(CustomPacketPayload payload, Object connection, Object player) {
        super(payload, connection);
        this.player = player;
    }

    public Object player() {
        return this.player;
    }
}
