package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.IllusionCapeLayer;
import com.Polarice3.Goety.client.render.layer.NamelessSetLayer;
import com.Polarice3.Goety.client.render.model.LichModeModel;
import com.Polarice3.Goety.common.entities.ally.Doppelganger;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;

public class DoppelgangerRenderer extends HumanoidMobRenderer<Doppelganger, PlayerModel<Doppelganger>> {
    private final PlayerModel<Doppelganger> normalModel;
    private final PlayerModel<Doppelganger> slimModel;
    private final PlayerModel<Doppelganger> lichModel;
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/lich.png");

    public DoppelgangerRenderer(EntityRendererProvider.Context p_174557_, boolean p_174558_) {
        super(p_174557_, new PlayerModel<>(p_174557_.bakeLayer(ModelLayers.PLAYER), p_174558_), 0.5F);
        this.normalModel = this.getModel();
        this.slimModel = new PlayerModel<>(p_174557_.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        this.lichModel = new LichModeModel<>(p_174557_.bakeLayer(ModModelLayer.LICH));
        this.addLayer(new IllusionCapeLayer(this));
        this.addLayer(new NamelessSetLayer<>(this, p_174557_.getModelSet()));
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(p_174557_.bakeLayer(p_174558_ ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel<>(p_174557_.bakeLayer(p_174558_ ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR))));
    }

    public void render(Doppelganger pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        this.setModelProperties(pEntity);
        if (LichdomHelper.isInLichMode(pEntity.getTrueOwner())){
            this.model = lichModel;
        } else if (pEntity.getModelName().equals("slim")) {
            this.model = slimModel;
        } else {
            this.model = normalModel;
        }
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    protected void scale(Doppelganger p_117798_, PoseStack p_117799_, float p_117800_) {
        float f = 0.9375F;
        p_117799_.scale(f, f, f);
    }

    public ResourceLocation getTextureLocation(Doppelganger entity) {
        if (LichdomHelper.isInLichMode(entity.getTrueOwner())){
            return TEXTURE;
        }
        return entity.getSkinTextureLocation();
    }

    public Vec3 getRenderOffset(Doppelganger p_117785_, float p_117786_) {
        return p_117785_.isCrouching() ? new Vec3(0.0D, -0.125D, 0.0D) : super.getRenderOffset(p_117785_, p_117786_);
    }

    private void setModelProperties(Doppelganger doppelganger) {
        PlayerModel<Doppelganger> playermodel = this.getModel();
        playermodel.setAllVisible(true);
        playermodel.hat.visible = doppelganger.isModelPartShown(PlayerModelPart.HAT);
        playermodel.jacket.visible = doppelganger.isModelPartShown(PlayerModelPart.JACKET);
        playermodel.leftPants.visible = doppelganger.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
        playermodel.rightPants.visible = doppelganger.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
        playermodel.leftSleeve.visible = doppelganger.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
        playermodel.rightSleeve.visible = doppelganger.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
        playermodel.crouching = doppelganger.isCrouching();
        HumanoidModel.ArmPose bipedmodel$armpose = getArmPose(doppelganger, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose bipedmodel$armpose1 = getArmPose(doppelganger, InteractionHand.OFF_HAND);
        if (bipedmodel$armpose.isTwoHanded()) {
            bipedmodel$armpose1 = doppelganger.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        }

        if (doppelganger.getMainArm() == HumanoidArm.RIGHT) {
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

    protected void setupRotations(Doppelganger p_117802_, PoseStack p_117803_, float p_117804_, float p_117805_, float p_117806_) {
        float f = p_117802_.getSwimAmount(p_117806_);
        if (p_117802_.isFallFlying()) {
            super.setupRotations(p_117802_, p_117803_, p_117804_, p_117805_, p_117806_);
            float f1 = (float)p_117802_.getFallFlyingTicks() + p_117806_;
            float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (!p_117802_.isAutoSpinAttack()) {
                p_117803_.mulPose(Vector3f.XP.rotationDegrees(f2 * (-90.0F - p_117802_.getXRot())));
            }

            Vec3 vec3 = p_117802_.getViewVector(p_117806_);
            Vec3 vec31 = p_117802_.getDeltaMovement();
            double d0 = vec31.horizontalDistanceSqr();
            double d1 = vec3.horizontalDistanceSqr();
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
                double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
                p_117803_.mulPose(Vector3f.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
            }
        } else if (f > 0.0F) {
            super.setupRotations(p_117802_, p_117803_, p_117804_, p_117805_, p_117806_);
            float f3 = p_117802_.isInWater() || p_117802_.isInFluidType((fluidType, height) -> p_117802_.canSwimInFluidType(fluidType)) ? -90.0F - p_117802_.getXRot() : -90.0F;
            float f4 = Mth.lerp(f, 0.0F, f3);
            p_117803_.mulPose(Vector3f.XP.rotationDegrees(f4));
            if (p_117802_.isVisuallySwimming()) {
                p_117803_.translate(0.0D, -1.0D, (double)0.3F);
            }
        } else {
            super.setupRotations(p_117802_, p_117803_, p_117804_, p_117805_, p_117806_);
        }

    }

}
