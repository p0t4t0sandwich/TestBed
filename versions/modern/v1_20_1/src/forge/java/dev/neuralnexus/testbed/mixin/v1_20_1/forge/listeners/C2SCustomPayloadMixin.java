/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@sperrer.ca The project is Licensed under <a
 * href="https://github.com/p0t4t0sandwich/TaterLib/blob/dev/LICENSE">MIT</a>
 */
package dev.neuralnexus.testbed.mixin.v1_20_1.forge.listeners;

import dev.neuralnexus.bedapi.CustomPacketPayload;
import dev.neuralnexus.bedapi.event.C2SCustomPacketEvent;
import dev.neuralnexus.bedapi.event.NetworkEvents;
import dev.neuralnexus.testbed.mixin.v1_20_1.forge.accessors.ServerGamePacketListenerImplAccessor;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 900)
public class C2SCustomPayloadMixin {
    @Shadow public ServerPlayer player;

    /**
     * Called when a custom payload packet is received from the client.
     *
     * @param packet The packet.
     * @param ci The callback info.
     */
    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    public void onC2SCustomPacket(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (this.player == null) return;
        CustomPacketPayload customPacket = (CustomPacketPayload) packet;
        Connection connection =
                ((ServerGamePacketListenerImplAccessor) this.player.connection)
                        .accessor$connection();
        Player player = this.player;
        NetworkEvents.C2S_CUSTOM_PACKET.invoke(
                new C2SCustomPacketEvent(customPacket, connection, player));
    }
}
