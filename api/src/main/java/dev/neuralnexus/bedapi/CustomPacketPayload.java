package dev.neuralnexus.bedapi;

public interface CustomPacketPayload {
    ResourceLocation channel();

    FriendlyByteBuf data();
}
