package dev.neuralnexus.testbed.mixin.v1_20_1.forge.api;

import dev.neuralnexus.bedapi.ResourceLocation;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Interface.Remap;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = net.minecraft.resources.ResourceLocation.class, priority = 1100)
@Implements(@Interface(iface = ResourceLocation.class, prefix = "rl$", remap = Remap.NONE))
public abstract class ResourceLocation_API {
    // @spotless:off
    @Shadow public abstract String shadow$getPath();
    @Shadow public abstract String shadow$getNamespace();
    // @spotless:on

    @Intrinsic
    public String rl$value() {
        return this.shadow$getPath();
    }

    @Intrinsic
    public String rl$namespace() {
        return this.shadow$getNamespace();
    }
}
