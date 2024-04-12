package com.Polarice3.Goety.mixin;

import net.minecraftforge.client.loading.ClientModLoader;
import net.minecraftforge.fml.LoadingFailedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientModLoader.class)
public interface ClientModLoaderAccessor {

    @Accessor(value = "error", remap = false)
    static LoadingFailedException getError() {
        throw new IllegalStateException("Failed to inject Accessor");
    }
}
