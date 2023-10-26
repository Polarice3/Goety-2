package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.block.ModChestRenderer;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.resources.model.Material;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

import java.util.Optional;

/**
 * Based on @TeamTwilight's AtlasGenerator
 */
public class ModAtlasProvider extends SpriteSourceProvider {
    public ModAtlasProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, helper, Goety.MOD_ID);
    }

    @Override
    protected void addSources() {
        ModChestRenderer.MATERIALS.values().stream().flatMap(e -> e.values().stream()).map(Material::texture)
                .forEach(resourceLocation -> this.atlas(CHESTS_ATLAS).addSource(new SingleFile(resourceLocation, Optional.empty())));
    }
}
