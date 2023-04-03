package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.GrandLavaball;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GrandLavaballRenderer extends ExplosiveProjectileRenderer<GrandLavaball> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Goety.MOD_ID,"textures/entity/projectiles/grand_lavaball.png");

    public GrandLavaballRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    public ResourceLocation getTextureLocation(GrandLavaball entity) {
        return TEXTURE;
    }
}
