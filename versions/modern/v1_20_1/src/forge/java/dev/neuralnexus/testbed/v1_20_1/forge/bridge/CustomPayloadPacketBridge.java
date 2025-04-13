package dev.neuralnexus.testbed.v1_20_1.forge.bridge;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface CustomPayloadPacketBridge {
    ResourceLocation bridge$identifier();

    FriendlyByteBuf bridge$data();
}
