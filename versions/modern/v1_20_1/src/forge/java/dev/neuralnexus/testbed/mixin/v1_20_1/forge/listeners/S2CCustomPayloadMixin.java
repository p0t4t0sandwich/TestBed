/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@sperrer.ca The project is Licensed under <a
 * href="https://github.com/p0t4t0sandwich/TaterLib/blob/dev/LICENSE">MIT</a>
 */
package dev.neuralnexus.testbed.mixin.v1_20_1.forge.listeners;

import dev.neuralnexus.bedapi.CustomPacketPayload;
import dev.neuralnexus.bedapi.event.NetworkEvents;
import dev.neuralnexus.bedapi.event.S2CCustomPacketEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPacketListener.class, priority = 900)
public abstract class S2CCustomPayloadMixin {
    /**
     * Called when a custom payload packet is received from the server.
     *
     * @param packet The packet.
     * @param ci The callback info.
     */
    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    public void onS2CCustomPacket(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
        CustomPacketPayload customPacket = (CustomPacketPayload) packet;
        if (Minecraft.getInstance().getConnection() == null) return;
        Connection connection = Minecraft.getInstance().getConnection().getConnection();
        Minecraft server = Minecraft.getInstance();
        NetworkEvents.S2C_CUSTOM_PACKET.invoke(
                new S2CCustomPacketEvent(customPacket, connection, server));
    }
}
