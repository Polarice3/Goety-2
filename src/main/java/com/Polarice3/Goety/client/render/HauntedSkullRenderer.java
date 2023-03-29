package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.client.render.model.HauntedSkullModel;
import com.Polarice3.Goety.common.entities.ally.HauntedSkull;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class HauntedSkullRenderer extends MobRenderer<HauntedSkull, HauntedSkullModel<HauntedSkull>> {

    public HauntedSkullRenderer(EntityRendererProvider.Context p_174435_) {
        super(p_174435_, new HauntedSkullModel<>(p_174435_.bakeLayer(ModModelLayer.HAUNTED_SKULL)), 0.3F);
    }

    protected int getBlockLightLevel(HauntedSkull p_116298_, BlockPos p_116299_) {
        return 15;
    }

    public ResourceLocation getTextureLocation(HauntedSkull p_116292_) {
        return p_116292_.getResourceLocation();
    }
}
