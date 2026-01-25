package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class FrayedRenderer extends ZombieRenderer {
    private static final ResourceLocation ZOMBIE_LOCATION = Goety.location("textures/entity/servants/zombie/frayed_hostile.png");

    public FrayedRenderer(EntityRendererProvider.Context p_174456_) {
        super(p_174456_);
    }

    @Override
    protected boolean isShaking(Zombie p_113773_) {
        return super.isShaking(p_113773_) || p_113773_.tickCount % 100 > 80;
    }

    public ResourceLocation getTextureLocation(Zombie p_114905_) {
        return ZOMBIE_LOCATION;
    }
}
