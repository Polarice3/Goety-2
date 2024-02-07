package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.BerserkFungus;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BerserkFungusRenderer extends AbstractFungusRenderer<BerserkFungus> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/projectiles/berserk_fungus.png");

    public BerserkFungusRenderer(EntityRendererProvider.Context p_174426_) {
        super(p_174426_);
    }

    protected void scale(BerserkFungus p_116294_, PoseStack p_116295_, float p_116296_) {
        p_116295_.scale(0.5F, 0.5F, 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(BerserkFungus p_114482_) {
        return TEXTURE;
    }
}
