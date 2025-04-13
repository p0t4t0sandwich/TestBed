package dev.neuralnexus.testbed.mixin.v1_20_1.forge.api;

import dev.neuralnexus.bedapi.FriendlyByteBuf;
import dev.neuralnexus.bedapi.ResourceLocation;

import io.netty.buffer.ByteBuf;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Interface.Remap;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = net.minecraft.network.FriendlyByteBuf.class, priority = 1100)
@Implements(@Interface(iface = FriendlyByteBuf.class, prefix = "buf$", remap = Remap.NONE))
public abstract class FriendlyByteBuf_API extends ByteBuf {
    // @spotless:off
    @Shadow public abstract net.minecraft.resources.ResourceLocation shadow$readResourceLocation();
    // @spotless:on

    @Intrinsic
    public ResourceLocation buf$readResourceLocation() {
        return (ResourceLocation) this.shadow$readResourceLocation();
    }
}
