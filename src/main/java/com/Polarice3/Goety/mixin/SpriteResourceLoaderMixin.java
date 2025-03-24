package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.Goety;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//Based on @AlexModGuy's codes: https://github.com/AlexModGuy/AlexsCaves/blob/main/src/main/java/com/github/alexmodguy/alexscaves/mixin/client/SpriteResourceLoaderMixin.java
@Mixin(SpriteResourceLoader.class)
public abstract class SpriteResourceLoaderMixin {

    @Inject(method = "load",
            at = @At("RETURN"))
    private static void ac_load(ResourceManager resourceManager, ResourceLocation location, CallbackInfoReturnable<SpriteResourceLoader> cir) {
        if (location.getPath().equals("armor_trims")) {
            SpriteResourceLoader ret = cir.getReturnValue();
            //Used AccessTransformer instead of Accessor because the latter keeps CRASHING FOR SOME PEOPLE, YET WORKS FINE FOR OTHERS, FOR SOME REASON
            for (SpriteSource source : ret.sources) {
                if (source instanceof PalettedPermutationsAccessor permutations && permutations.getPaletteKey().getPath().equals("trims/color_palettes/trim_palette")) {
                    ResourceLocation trimLocation = Goety.location("trims/models/armor/crypt");
                    ResourceLocation leggingsTrimLocation = Goety.location("trims/models/armor/crypt").withSuffix("_leggings");
                    permutations.setTextures(ImmutableList.<ResourceLocation>builder().addAll(permutations.getTextures()).add(trimLocation, leggingsTrimLocation).build());
                }
            }
        }
    }
}
