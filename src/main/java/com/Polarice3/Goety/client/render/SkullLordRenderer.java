package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SkullLordModel;
import com.Polarice3.Goety.common.entities.hostile.SkullLord;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SkullLordRenderer extends MobRenderer<SkullLord, SkullLordModel<SkullLord>> {
    public static final ResourceLocation CONNECTION = Goety.location("textures/entity/skull_lord/skull_lord_connection.png");
    private static final ResourceLocation LOCATION = Goety.location("textures/entity/skull_lord/skull_lord.png");
    private static final ResourceLocation VULNERABLE = Goety.location("textures/entity/skull_lord/skull_lord_vulnerable.png");
    private static final ResourceLocation CHARGE = Goety.location("textures/entity/skull_lord/skull_lord_charging.png");

    public SkullLordRenderer(EntityRendererProvider.Context p_174435_) {
        super(p_174435_, new SkullLordModel<>(p_174435_.bakeLayer(ModModelLayer.SKULL_LORD)), 0.5F);
    }

    protected int getBlockLightLevel(SkullLord pEntity, BlockPos pPos) {
        return 15;
    }

    protected float getWhiteOverlayProgress(SkullLord p_114043_, float p_114044_) {
        if (p_114043_.isShockWave()) {
            float f = p_114043_.getSwelling(p_114044_);
            return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
        } else {
            return super.getWhiteOverlayProgress(p_114043_, p_114044_);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(SkullLord pEntity) {
        if (pEntity.isCharging()){
            return CHARGE;
        } else {
            return pEntity.isInvulnerable() ? LOCATION: VULNERABLE;
        }
    }
}
