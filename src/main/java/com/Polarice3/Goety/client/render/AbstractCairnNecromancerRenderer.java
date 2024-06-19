package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.config.MobsConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class AbstractCairnNecromancerRenderer extends AbstractNecromancerRenderer{
    private static final ResourceLocation SKELETON_LOCATION = Goety.location("textures/entity/necromancer/frost_necromancer.png");
    private static final ResourceLocation SERVANT_LOCATION = Goety.location("textures/entity/necromancer/frost_necromancer_servant.png");

    public AbstractCairnNecromancerRenderer(EntityRendererProvider.Context p_174380_) {
        super(p_174380_);
        this.addLayer(new NecromancerEyesLayer<>(this, Goety.location("textures/entity/necromancer/frost_necromancer_glow.png")));
    }

    public ResourceLocation getTextureLocation(AbstractNecromancer p_115941_) {
        if (p_115941_.isHostile() || !MobsConfig.NecromancerServantTexture.get()){
            return SKELETON_LOCATION;
        } else {
            return SERVANT_LOCATION;
        }
    }
}
