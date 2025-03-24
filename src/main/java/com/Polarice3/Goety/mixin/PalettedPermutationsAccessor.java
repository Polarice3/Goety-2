package com.Polarice3.Goety.mixin;

import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PalettedPermutations.class)
public interface PalettedPermutationsAccessor {
    @Accessor
    List<ResourceLocation> getTextures();

    @Accessor("textures")
    @Mutable
    void setTextures(List<ResourceLocation> value);

    @Accessor
    ResourceLocation getPaletteKey();
}
