package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CairnNecromancerRenderer extends AbstractNecromancerRenderer{
    private static final ResourceLocation SKELETON_LOCATION = Goety.location("textures/entity/necromancer/frost_necromancer.png");

    public CairnNecromancerRenderer(EntityRendererProvider.Context p_174380_) {
        super(p_174380_);
        this.addLayer(new NecromancerEyesLayer<>(this, Goety.location("textures/entity/necromancer/frost_necromancer_glow.png")));
    }

    public ResourceLocation getTextureLocation(AbstractNecromancer p_115941_) {
        return SKELETON_LOCATION;
    }
}
