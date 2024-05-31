package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.MagmaBomb;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class MagmaBombRenderer extends AbstractFungusRenderer<MagmaBomb> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/projectiles/pyroclast.png");

    public MagmaBombRenderer(EntityRendererProvider.Context p_174426_) {
        super(p_174426_);
    }

    protected void scale(MagmaBomb p_116294_, PoseStack p_116295_, float p_116296_) {
        p_116295_.scale(1.5F, 1.5F, 1.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MagmaBomb p_114482_) {
        return TEXTURE;
    }
}
