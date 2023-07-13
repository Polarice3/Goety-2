package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class NecromancerRenderer extends AbstractNecromancerRenderer{
    public NecromancerRenderer(EntityRendererProvider.Context p_174380_) {
        super(p_174380_);
        this.addLayer(new NecromancerEyesLayer<>(this, Goety.location("textures/entity/necromancer_glow.png")));
        this.addLayer(new NecroSetLayer<>(this, p_174380_.getModelSet(), Goety.location("textures/models/curios/necro_cape.png")));
    }
}
