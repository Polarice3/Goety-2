package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.common.entities.ally.Doppelganger;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;

public class DoppelgangerRenderer extends HumanoidMobRenderer<Doppelganger, PlayerModel<Doppelganger>> {

    public DoppelgangerRenderer(EntityRendererProvider.Context p_174557_, boolean p_174558_) {
        super(p_174557_, new PlayerModel<>(p_174557_.bakeLayer(p_174558_ ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), p_174558_), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel(p_174557_.bakeLayer(p_174558_ ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(p_174557_.bakeLayer(p_174558_ ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR))));
    }

    public void render(Doppelganger pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        this.setModelProperties(pEntity);
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    public ResourceLocation getTextureLocation(Doppelganger entity) {
        if (entity.getTrueOwner() != null){
            if (entity.getTrueOwner() instanceof AbstractClientPlayer){
                return ((AbstractClientPlayer) entity.getTrueOwner()).getSkinTextureLocation();
            } else {
                return DefaultPlayerSkin.getDefaultSkin(entity.getUUID());
            }
        } else {
            return DefaultPlayerSkin.getDefaultSkin(entity.getUUID());
        }
    }

    private void setModelProperties(Doppelganger pClientPlayer) {
        PlayerModel<Doppelganger> playermodel = this.getModel();
        HumanoidModel.ArmPose bipedmodel$armpose = getArmPose(pClientPlayer, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose bipedmodel$armpose1 = getArmPose(pClientPlayer, InteractionHand.OFF_HAND);
        if (bipedmodel$armpose.isTwoHanded()) {
            bipedmodel$armpose1 = pClientPlayer.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        }

        if (pClientPlayer.getMainArm() == HumanoidArm.RIGHT) {
            playermodel.rightArmPose = bipedmodel$armpose;
            playermodel.leftArmPose = bipedmodel$armpose1;
        } else {
            playermodel.rightArmPose = bipedmodel$armpose1;
            playermodel.leftArmPose = bipedmodel$armpose;
        }
    }

    private static HumanoidModel.ArmPose getArmPose(Doppelganger p_241741_0_, InteractionHand p_241741_1_) {
        ItemStack itemstack = p_241741_0_.getItemInHand(p_241741_1_);
        if (itemstack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        } else {
            if (p_241741_0_.getUsedItemHand() == p_241741_1_ && p_241741_0_.getUseItemRemainingTicks() > 0) {
                UseAnim useaction = itemstack.getUseAnimation();
                if (useaction == UseAnim.BLOCK) {
                    return HumanoidModel.ArmPose.BLOCK;
                }

                if (useaction == UseAnim.BOW) {
                    return HumanoidModel.ArmPose.BOW_AND_ARROW;
                }

                if (useaction == UseAnim.SPEAR) {
                    return HumanoidModel.ArmPose.THROW_SPEAR;
                }

                if (useaction == UseAnim.CROSSBOW && p_241741_1_ == p_241741_0_.getUsedItemHand()) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else if (!p_241741_0_.swinging && itemstack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            return HumanoidModel.ArmPose.ITEM;
        }
    }

}
