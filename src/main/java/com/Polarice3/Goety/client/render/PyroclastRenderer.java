package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.Pyroclast;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class PyroclastRenderer extends AbstractFungusRenderer<Pyroclast> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/projectiles/pyroclast.png");

    public PyroclastRenderer(EntityRendererProvider.Context p_174426_) {
        super(p_174426_);
    }

    @Override
    public ResourceLocation getTextureLocation(Pyroclast p_114482_) {
        return TEXTURE;
    }
}
