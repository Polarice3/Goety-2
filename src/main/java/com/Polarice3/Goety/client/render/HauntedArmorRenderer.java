package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.HauntedArmorModel;
import com.Polarice3.Goety.common.entities.neutral.AbstractHauntedArmor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public class HauntedArmorRenderer extends HumanoidMobRenderer<AbstractHauntedArmor, HauntedArmorModel> {
    private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation(Goety.MOD_ID, "textures/entity/haunted_armor.png");

    public HauntedArmorRenderer(EntityRendererProvider.Context p_174557_) {
        super(p_174557_, new HauntedArmorModel(p_174557_.bakeLayer(ModelLayers.PLAYER)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(p_174557_.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel<>(p_174557_.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
    }

    public void render(AbstractHauntedArmor pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        this.setModelProperties(pEntity);
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    private void setModelProperties(AbstractHauntedArmor pEntity) {
        HauntedArmorModel playermodel = this.getModel();
        playermodel.setAllVisible(false);
        HumanoidModel.ArmPose bipedmodel$armpose = getArmPose(pEntity, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose bipedmodel$armpose1 = getArmPose(pEntity, InteractionHand.OFF_HAND);
        if (bipedmodel$armpose.isTwoHanded()) {
            bipedmodel$armpose1 = pEntity.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        }

        if (pEntity.getMainArm() == HumanoidArm.RIGHT) {
            playermodel.rightArmPose = bipedmodel$armpose;
            playermodel.leftArmPose = bipedmodel$armpose1;
        } else {
            playermodel.rightArmPose = bipedmodel$armpose1;
            playermodel.leftArmPose = bipedmodel$armpose;
        }
    }

    private static HauntedArmorModel.ArmPose getArmPose(AbstractHauntedArmor p_241741_0_, InteractionHand p_241741_1_) {
        ItemStack itemstack = p_241741_0_.getItemInHand(p_241741_1_);
        if (p_241741_0_.getUsedItemHand() == p_241741_1_ && p_241741_0_.getUseItemRemainingTicks() > 0) {
            UseAnim useaction = itemstack.getUseAnimation();
            if (useaction == UseAnim.BLOCK) {
                return HumanoidModel.ArmPose.BLOCK;
            }
        }
        return HumanoidModel.ArmPose.EMPTY;
    }

    public ResourceLocation getTextureLocation(AbstractHauntedArmor entity) {
        return ZOMBIE_LOCATION;
    }
}
