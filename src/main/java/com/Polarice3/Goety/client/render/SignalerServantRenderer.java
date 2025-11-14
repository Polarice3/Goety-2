package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.Polarice3.Goety.client.render.model.IllagerServantModel;
import com.Polarice3.Goety.common.entities.ally.illager.SignalerServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpyglassItem;

public class SignalerServantRenderer<T extends SignalerServant> extends MobRenderer<T, IllagerServantModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/illager/signaler.png");

    public SignalerServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new IllagerServantModel<>(renderManagerIn.bakeLayer(ModModelLayer.ILLAGER_SERVANT)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, renderManagerIn));
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()){
            public void renderArmWithItem(LivingEntity entity, ItemStack stack, ItemDisplayContext p_174527_,
                                          HumanoidArm p_174528_, PoseStack p_174529_, MultiBufferSource p_174530_, int p_174531_) {
                if (entity.getUseItem().getItem() instanceof SpyglassItem && entity.getUseItem() == stack) {
                    this.renderArmWithSpyglass(entity, stack, p_174528_, p_174529_, p_174530_, p_174531_);
                } else {
                    super.renderArmWithItem(entity, stack, p_174527_, p_174528_, p_174529_, p_174530_, p_174531_);
                }
            }

            private void renderArmWithSpyglass(LivingEntity p_174518_, ItemStack p_174519_, HumanoidArm p_174520_,
                                               PoseStack p_174521_, MultiBufferSource p_174522_, int p_174523_) {
                p_174521_.pushPose();
                ModelPart modelpart = this.getParentModel().getHead();
                float f = modelpart.xRot;
                modelpart.xRot = Mth.clamp(modelpart.xRot, (-(float) Math.PI / 6F), ((float) Math.PI / 2F));
                modelpart.translateAndRotate(p_174521_);
                modelpart.xRot = f;
                CustomHeadLayer.translateToHead(p_174521_, false);
                boolean flag = p_174520_ == HumanoidArm.LEFT;
                p_174521_.translate((double) ((flag ? -2.5F : 2.5F) / 16.0F), -0.0625D, 0.0D);
                Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(p_174518_, p_174519_,
                        ItemDisplayContext.HEAD, false, p_174521_, p_174522_, p_174523_);
                p_174521_.popPose();
            }
        });
    }

    protected void scale(T entity, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
