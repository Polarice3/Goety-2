package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

import java.util.Optional;

/**
 * Based on @TeamLapen's Werewolf Rendering codes: <a href="https://github.com/TeamLapen/Werewolves/blob/1.20/src/main/java/de/teamlapen/werewolves/client/render/WerewolfPlayerRenderer.java">...</a>
 */
public abstract class LichPlayerRenderer <T extends AbstractClientPlayer, E extends PlayerModel<T>> extends PlayerRenderer {

    public static Optional<String> getLichRenderer(AbstractClientPlayer player) {
        if (LichdomHelper.isLich(player)) {
            if (LichdomHelper.isInLichMode(player)) {
                return Optional.of(Goety.MOD_ID +":lich");
            }
        }
        return Optional.empty();
    }

    public LichPlayerRenderer(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> model) {
        super(context, true);
        this.model = model;
    }

    public void render(AbstractClientPlayer p_117788_, float p_117789_, float p_117790_, PoseStack p_117791_, MultiBufferSource p_117792_, int p_117793_) {
        this.setModelProperties(p_117788_);
        p_117791_.pushPose();
        this.model.attackTime = this.getAttackAnim(p_117788_, p_117790_);

        boolean shouldSit = p_117788_.isPassenger() && (p_117788_.getVehicle() != null && p_117788_.getVehicle().shouldRiderSit());
        this.model.riding = shouldSit;
        this.model.young = p_117788_.isBaby();
        float f = Mth.rotLerp(p_117790_, p_117788_.yBodyRotO, p_117788_.yBodyRot);
        float f1 = Mth.rotLerp(p_117790_, p_117788_.yHeadRotO, p_117788_.yHeadRot);
        float f2 = f1 - f;
        if (shouldSit && p_117788_.getVehicle() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)p_117788_.getVehicle();
            f = Mth.rotLerp(p_117790_, livingentity.yBodyRotO, livingentity.yBodyRot);
            f2 = f1 - f;
            float f3 = Mth.wrapDegrees(f2);
            if (f3 < -85.0F) {
                f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
                f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
                f += f3 * 0.2F;
            }

            f2 = f1 - f;
        }

        float f6 = Mth.lerp(p_117790_, p_117788_.xRotO, p_117788_.getXRot());
        if (isEntityUpsideDown(p_117788_)) {
            f6 *= -1.0F;
            f2 *= -1.0F;
        }

        if (p_117788_.hasPose(Pose.SLEEPING)) {
            Direction direction = p_117788_.getBedOrientation();
            if (direction != null) {
                float f4 = p_117788_.getEyeHeight(Pose.STANDING) - 0.1F;
                p_117791_.translate((float)(-direction.getStepX()) * f4, 0.0F, (float)(-direction.getStepZ()) * f4);
            }
        }

        float f7 = this.getBob(p_117788_, p_117790_);
        this.setupRotations(p_117788_, p_117791_, f7, f, p_117790_);
        p_117791_.scale(-1.0F, -1.0F, 1.0F);
        this.scale(p_117788_, p_117791_, p_117790_);
        p_117791_.translate(0.0F, -1.501F, 0.0F);
        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && p_117788_.isAlive()) {
            f8 = p_117788_.walkAnimation.speed(p_117790_);
            f5 = p_117788_.walkAnimation.position(p_117790_);
            if (p_117788_.isBaby()) {
                f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }

        this.model.prepareMobModel(p_117788_, f5, f8, p_117790_);
        this.model.setupAnim(p_117788_, f5, f8, f7, f2, f6);
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = this.isBodyVisible(p_117788_);
        boolean flag1 = !flag && !p_117788_.isInvisibleTo(minecraft.player);
        boolean flag2 = minecraft.shouldEntityAppearGlowing(p_117788_);
        RenderType rendertype = this.getRenderType(p_117788_, flag, flag1, flag2);
        if (rendertype != null) {
            VertexConsumer vertexconsumer = p_117792_.getBuffer(rendertype);
            int i = getOverlayCoords(p_117788_, this.getWhiteOverlayProgress(p_117788_, p_117790_));
            this.model.renderToBuffer(p_117791_, vertexconsumer, p_117793_, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
        }

        if (!p_117788_.isSpectator()) {
            for(RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderlayer : this.layers) {
                renderlayer.render(p_117791_, p_117792_, p_117793_, p_117788_, f5, f8, p_117790_, f7, f2, f6);
            }
        }

        p_117791_.popPose();
    }

    private void setModelProperties(AbstractClientPlayer p_117819_) {
        PlayerModel<AbstractClientPlayer> playermodel = this.getModel();
        if (p_117819_.isSpectator()) {
            playermodel.setAllVisible(false);
            playermodel.head.visible = true;
            playermodel.hat.visible = true;
        } else {
            playermodel.setAllVisible(true);
            playermodel.hat.visible = p_117819_.isModelPartShown(PlayerModelPart.HAT);
            playermodel.jacket.visible = p_117819_.isModelPartShown(PlayerModelPart.JACKET);
            playermodel.leftPants.visible = p_117819_.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            playermodel.rightPants.visible = p_117819_.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            playermodel.leftSleeve.visible = p_117819_.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            playermodel.rightSleeve.visible = p_117819_.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            playermodel.crouching = p_117819_.isCrouching();
            HumanoidModel.ArmPose humanoidmodel$armpose = getArmPose(p_117819_, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose humanoidmodel$armpose1 = getArmPose(p_117819_, InteractionHand.OFF_HAND);
            if (humanoidmodel$armpose.isTwoHanded()) {
                humanoidmodel$armpose1 = p_117819_.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }

            if (p_117819_.getMainArm() == HumanoidArm.RIGHT) {
                playermodel.rightArmPose = humanoidmodel$armpose;
                playermodel.leftArmPose = humanoidmodel$armpose1;
            } else {
                playermodel.rightArmPose = humanoidmodel$armpose1;
                playermodel.leftArmPose = humanoidmodel$armpose;
            }
        }

    }

    private static HumanoidModel.ArmPose getArmPose(AbstractClientPlayer p_117795_, InteractionHand p_117796_) {
        ItemStack itemstack = p_117795_.getItemInHand(p_117796_);
        if (itemstack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        } else {
            if (p_117795_.getUsedItemHand() == p_117796_ && p_117795_.getUseItemRemainingTicks() > 0) {
                UseAnim useanim = itemstack.getUseAnimation();
                if (useanim == UseAnim.BLOCK) {
                    return HumanoidModel.ArmPose.BLOCK;
                }

                if (useanim == UseAnim.BOW) {
                    return HumanoidModel.ArmPose.BOW_AND_ARROW;
                }

                if (useanim == UseAnim.SPEAR) {
                    return HumanoidModel.ArmPose.THROW_SPEAR;
                }

                if (useanim == UseAnim.CROSSBOW && p_117796_ == p_117795_.getUsedItemHand()) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }

                if (useanim == UseAnim.SPYGLASS) {
                    return HumanoidModel.ArmPose.SPYGLASS;
                }

                if (useanim == UseAnim.TOOT_HORN) {
                    return HumanoidModel.ArmPose.TOOT_HORN;
                }

                if (useanim == UseAnim.BRUSH) {
                    return HumanoidModel.ArmPose.BRUSH;
                }
            } else if (!p_117795_.swinging && itemstack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            HumanoidModel.ArmPose forgeArmPose = net.minecraftforge.client.extensions.common.IClientItemExtensions.of(itemstack).getArmPose(p_117795_, p_117796_, itemstack);
            if (forgeArmPose != null) return forgeArmPose;

            return HumanoidModel.ArmPose.ITEM;
        }
    }

}
