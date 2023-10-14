package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.HauntModel;
import com.Polarice3.Goety.common.entities.ally.Haunt;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class HauntRenderer extends MobRenderer<Haunt, HauntModel<Haunt>> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/grave_golem/haunt.png");

    public HauntRenderer(EntityRendererProvider.Context p_174435_) {
        super(p_174435_, new HauntModel<>(p_174435_.bakeLayer(ModModelLayer.HAUNT)), 0.3F);
    }

    protected int getBlockLightLevel(Haunt p_116298_, BlockPos p_116299_) {
        return 15;
    }

    public ResourceLocation getTextureLocation(Haunt p_116292_) {
        return TEXTURE;
    }
}
