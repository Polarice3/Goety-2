package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.common.entities.hostile.BoneLord;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class BoneLordRenderer extends HumanoidMobRenderer<BoneLord, SkeletonModel<BoneLord>> {
    private static final ResourceLocation SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/skeleton.png");

    public BoneLordRenderer(EntityRendererProvider.Context p_174380_) {
        this(p_174380_, ModelLayers.SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
    }

    public BoneLordRenderer(EntityRendererProvider.Context p_174382_, ModelLayerLocation p_174383_, ModelLayerLocation p_174384_, ModelLayerLocation p_174385_) {
        super(p_174382_, new SkeletonModel<>(p_174382_.bakeLayer(p_174383_)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new SkeletonModel<>(p_174382_.bakeLayer(p_174384_)), new SkeletonModel<>(p_174382_.bakeLayer(p_174385_))){
            protected void setPartVisibility(SkeletonModel p_117126_, EquipmentSlot p_117127_) {
                p_117126_.setAllVisible(false);
                switch (p_117127_) {
                    case HEAD:
                        p_117126_.head.visible = false;
                        p_117126_.hat.visible = false;
                        break;
                    case CHEST:
                        p_117126_.body.visible = true;
                        p_117126_.rightArm.visible = true;
                        p_117126_.leftArm.visible = true;
                        break;
                    case LEGS:
                        p_117126_.body.visible = true;
                        p_117126_.rightLeg.visible = true;
                        p_117126_.leftLeg.visible = true;
                        break;
                    case FEET:
                        p_117126_.rightLeg.visible = true;
                        p_117126_.leftLeg.visible = true;
                }

            }
        });
    }

    public void render(BoneLord pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        SkeletonModel<BoneLord> skeletonModel = this.getModel();
        skeletonModel.head.visible = false;
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    public ResourceLocation getTextureLocation(BoneLord entity) {
        return SKELETON_LOCATION;
    }
}
