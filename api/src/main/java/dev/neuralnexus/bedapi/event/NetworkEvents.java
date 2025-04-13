package dev.neuralnexus.bedapi.event;

import dev.neuralnexus.taterapi.event.EventManager;

public class NetworkEvents {
    /** Called when a custom packet is received from the client to the server. */
    public static final EventManager<C2SCustomPacketEvent> C2S_CUSTOM_PACKET =
            new EventManager<>(C2SCustomPacketEvent.class);

    /** Called when a custom packet is received from the server to the client. */
    public static final EventManager<S2CCustomPacketEvent> S2C_CUSTOM_PACKET =
            new EventManager<>(S2CCustomPacketEvent.class);
}
