package dev.neuralnexus.testbed.mixin.v1_20_1.forge.api;

import dev.neuralnexus.bedapi.CustomPacketPayload;
import dev.neuralnexus.bedapi.FriendlyByteBuf;
import dev.neuralnexus.bedapi.ResourceLocation;

import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Interface.Remap;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(
        value = {ClientboundCustomPayloadPacket.class, ServerboundCustomPayloadPacket.class},
        priority = 1100)
@Implements(@Interface(iface = CustomPacketPayload.class, prefix = "packet$", remap = Remap.NONE))
public abstract class CustomPayloadPacket_API {
    @Intrinsic
    public ResourceLocation packet$channel() {
        Object self = this;
        if (self instanceof ClientboundCustomPayloadPacket client) {
            return (ResourceLocation) client.getIdentifier();
        } else if (self instanceof ServerboundCustomPayloadPacket server) {
            return (ResourceLocation) server.getIdentifier();
        } else {
            throw new IllegalStateException("Unknown packet type");
        }
    }

    @Intrinsic
    public FriendlyByteBuf packet$data() {
        Object self = this;
        if (self instanceof ClientboundCustomPayloadPacket client) {
            return (FriendlyByteBuf) client.getData();
        } else if (self instanceof ServerboundCustomPayloadPacket server) {
            return (FriendlyByteBuf) server.getData();
        } else {
            throw new IllegalStateException("Unknown packet type");
        }
    }
}
