package dev.neuralnexus.testbed.mixin.v1_20_1.forge.accessors;

import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerGamePacketListenerImpl.class)
public interface ServerGamePacketListenerImplAccessor {
    @Accessor("connection")
    Connection accessor$connection();
}
