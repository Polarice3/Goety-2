package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.IrkModel;
import com.Polarice3.Goety.common.entities.hostile.Irk;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class IrkRenderer extends MobRenderer<Irk, IrkModel> {
    private static final ResourceLocation VEX_TEXTURE = Goety.location("textures/entity/illagers/irk.png");
    private static final ResourceLocation VEX_CHARGING_TEXTURE = Goety.location("textures/entity/illagers/irk_charging.png");

    public IrkRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new IrkModel(renderManagerIn.bakeLayer(ModModelLayer.IRK)), 0.3F);
    }

    protected int getBlockLightLevel(Irk entityIn, BlockPos partialTicks) {
        return 15;
    }

    public ResourceLocation getTextureLocation(Irk entity) {
        return entity.isCharging() ? VEX_CHARGING_TEXTURE : VEX_TEXTURE;
    }
}
