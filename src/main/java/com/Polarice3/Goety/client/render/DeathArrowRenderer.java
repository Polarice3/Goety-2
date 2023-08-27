package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.DeathArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DeathArrowRenderer extends ArrowRenderer<DeathArrow> {
    public static final ResourceLocation ARROW_LOCATION = Goety.location("textures/entity/projectiles/death_arrow.png");

    public DeathArrowRenderer(EntityRendererProvider.Context p_173917_) {
        super(p_173917_);
    }

    @Override
    public ResourceLocation getTextureLocation(DeathArrow p_114482_) {
        return ARROW_LOCATION;
    }
}
