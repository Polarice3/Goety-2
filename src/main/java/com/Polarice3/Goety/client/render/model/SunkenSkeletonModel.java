package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;

public class SunkenSkeletonModel<T extends Owned> extends HumanoidModel<T> {
   public SunkenSkeletonModel(ModelPart p_170941_) {
      super(p_170941_);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F)
              .texOffs(12, 29).addBox(-5.0F, 4.0F, -2.0F, 1.0F, 1.0F, 0.0F)
              .texOffs(12, 30).addBox(2.0F, 12.0F, -2.0F, 2.0F, 2.0F, 0.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
      partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
              .texOffs(32, 0).addBox(-1.0F, -16.0F, -4.0F, 9.0F, 10.0F, 0.0F)
              .texOffs(48, 0).addBox(-1.0F, -3.0F, 2.0F, 6.0F, 0.0F, 4.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
      partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
      partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
      partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F)
              .texOffs(24, 4).addBox(-2.0F, 4.0F, -2.0F, 2.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));
      partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(2.0F, 12.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public void prepareMobModel(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
      this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
      this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
      ItemStack itemstack = pEntity.getItemInHand(InteractionHand.MAIN_HAND);
      if (itemstack.getItem() == Items.BOW && pEntity.isAggressive()) {
         if (pEntity.getMainArm() == HumanoidArm.RIGHT) {
            this.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
         } else {
            this.leftArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
         }
      }
      if (pEntity.isAggressive()) {
         if (itemstack.getItem() == Items.CROSSBOW) {
            if (pEntity.getMainArm() == HumanoidArm.RIGHT) {
               this.rightArmPose = ArmPose.CROSSBOW_HOLD;
               if (pEntity.isChargingCrossbow()) {
                  this.rightArmPose = ArmPose.CROSSBOW_CHARGE;
               }
            } else {
               this.leftArmPose = ArmPose.CROSSBOW_HOLD;
               if (pEntity.isChargingCrossbow()) {
                  this.leftArmPose = ArmPose.CROSSBOW_CHARGE;
               }
            }
         }
      }

      super.prepareMobModel(pEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
   }

   public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
      ItemStack itemstack = pEntity.getMainHandItem();
      if (pEntity.isAggressive() && (!(itemstack.getItem() instanceof ProjectileWeaponItem))) {
         float f = Mth.sin(this.attackTime * (float)Math.PI);
         float f1 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float)Math.PI);
         this.rightArm.zRot = 0.0F;
         this.leftArm.zRot = 0.0F;
         this.rightArm.yRot = -(0.1F - f * 0.6F);
         this.leftArm.yRot = 0.1F - f * 0.6F;
         this.rightArm.xRot = (-(float)Math.PI / 2F);
         this.leftArm.xRot = (-(float)Math.PI / 2F);
         this.rightArm.xRot -= f * 1.2F - f1 * 0.4F;
         this.leftArm.xRot -= f * 1.2F - f1 * 0.4F;
         AnimationUtils.bobArms(this.rightArm, this.leftArm, pAgeInTicks);
      }
      boolean flag2 = pEntity.getMainArm() == HumanoidArm.RIGHT;
      boolean flag3 = flag2 ? this.leftArmPose.isTwoHanded() : this.rightArmPose.isTwoHanded();
      if (flag2 != flag3) {
         this.posingLeftArm(pEntity);
         this.posingRightArm(pEntity);
      } else {
         this.posingRightArm(pEntity);
         this.posingLeftArm(pEntity);
      }
      if (this.swimAmount > 0.0F) {
         this.rightArm.xRot = this.rotlerpRad(this.swimAmount, this.rightArm.xRot, -2.5132742F) + this.swimAmount * 0.35F * Mth.sin(0.1F * pAgeInTicks);
         this.leftArm.xRot = this.rotlerpRad(this.swimAmount, this.leftArm.xRot, -2.5132742F) - this.swimAmount * 0.35F * Mth.sin(0.1F * pAgeInTicks);
         this.rightArm.zRot = this.rotlerpRad(this.swimAmount, this.rightArm.zRot, -0.15F);
         this.leftArm.zRot = this.rotlerpRad(this.swimAmount, this.leftArm.zRot, 0.15F);
         this.leftLeg.xRot -= this.swimAmount * 0.55F * Mth.sin(0.1F * pAgeInTicks);
         this.rightLeg.xRot += this.swimAmount * 0.55F * Mth.sin(0.1F * pAgeInTicks);
         this.head.xRot = 0.0F;
      }
      this.hat.visible = false;
   }

   public void translateToHand(HumanoidArm pSide, PoseStack pMatrixStack) {
      float f = pSide == HumanoidArm.RIGHT ? 1.0F : -1.0F;
      ModelPart modelrenderer = this.getArm(pSide);
      modelrenderer.x += f;
      modelrenderer.translateAndRotate(pMatrixStack);
      modelrenderer.x -= f;
   }

   public void posingRightArm(T p_241654_1_) {
      switch (this.rightArmPose) {
         case EMPTY -> this.rightArm.yRot = 0.0F;
         case BLOCK -> {
            this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.9424779F;
            this.rightArm.yRot = (-(float) Math.PI / 6F);
         }
         case ITEM -> {
            this.rightArm.xRot = this.rightArm.xRot * 0.5F - ((float) Math.PI / 10F);
            this.rightArm.yRot = 0.0F;
         }
         case THROW_SPEAR -> {
            this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float) Math.PI;
            this.rightArm.yRot = 0.0F;
         }
         case BOW_AND_ARROW -> {
            this.rightArm.yRot = -0.1F + this.head.yRot;
            this.leftArm.yRot = 0.1F + this.head.yRot + 0.4F;
            this.rightArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
            this.leftArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
         }
         case CROSSBOW_CHARGE -> AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, p_241654_1_, true);
         case CROSSBOW_HOLD -> AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
      }

   }

   public void posingLeftArm(T p_241655_1_) {
      switch (this.leftArmPose) {
         case EMPTY -> this.leftArm.yRot = 0.0F;
         case BLOCK -> {
            this.leftArm.xRot = this.leftArm.xRot * 0.5F - 0.9424779F;
            this.leftArm.yRot = ((float) Math.PI / 6F);
         }
         case ITEM -> {
            this.leftArm.xRot = this.leftArm.xRot * 0.5F - ((float) Math.PI / 10F);
            this.leftArm.yRot = 0.0F;
         }
         case THROW_SPEAR -> {
            this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float) Math.PI;
            this.leftArm.yRot = 0.0F;
         }
         case BOW_AND_ARROW -> {
            this.rightArm.yRot = -0.1F + this.head.yRot - 0.4F;
            this.leftArm.yRot = 0.1F + this.head.yRot;
            this.rightArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
            this.leftArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
         }
         case CROSSBOW_CHARGE -> AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, p_241655_1_, false);
         case CROSSBOW_HOLD -> AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, false);
      }

   }
}