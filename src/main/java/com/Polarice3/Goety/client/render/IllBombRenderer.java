package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.IllBomb;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class IllBombRenderer extends ExplosiveProjectileRenderer<IllBomb> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Goety.MOD_ID,"textures/item/ill_bomb.png");

    public IllBombRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public float scale() {
        return 1.0F;
    }

    public ResourceLocation getTextureLocation(IllBomb entity) {
        return TEXTURE;
    }
}
