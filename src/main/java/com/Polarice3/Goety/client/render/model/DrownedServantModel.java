package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.ally.ZombieServant;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DrownedServantModel<T extends ZombieServant> extends ZombieServantModel<T> {
   public DrownedServantModel(ModelPart p_170534_) {
      super(p_170534_);
   }

   public static LayerDefinition createBodyLayer(CubeDeformation p_170536_) {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(p_170536_, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170536_), PartPose.offset(5.0F, 2.0F, 0.0F));
      partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170536_), PartPose.offset(1.9F, 12.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void prepareMobModel(T p_102521_, float p_102522_, float p_102523_, float p_102524_) {
      this.rightArmPose = ArmPose.EMPTY;
      this.leftArmPose = ArmPose.EMPTY;
      ItemStack itemstack = p_102521_.getItemInHand(InteractionHand.MAIN_HAND);
      if (itemstack.is(Items.TRIDENT) && p_102521_.isAggressive()) {
         if (p_102521_.getMainArm() == HumanoidArm.RIGHT) {
            this.rightArmPose = ArmPose.THROW_SPEAR;
         } else {
            this.leftArmPose = ArmPose.THROW_SPEAR;
         }
      }

      super.prepareMobModel(p_102521_, p_102522_, p_102523_, p_102524_);
   }

   public void setupAnim(T p_102526_, float p_102527_, float p_102528_, float p_102529_, float p_102530_, float p_102531_) {
      super.setupAnim(p_102526_, p_102527_, p_102528_, p_102529_, p_102530_, p_102531_);
      if (this.leftArmPose == ArmPose.THROW_SPEAR) {
         this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float)Math.PI;
         this.leftArm.yRot = 0.0F;
      }

      if (this.rightArmPose == ArmPose.THROW_SPEAR) {
         this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float)Math.PI;
         this.rightArm.yRot = 0.0F;
      }

      if (this.swimAmount > 0.0F) {
         this.rightArm.xRot = this.rotlerpRad(this.swimAmount, this.rightArm.xRot, -2.5132742F) + this.swimAmount * 0.35F * Mth.sin(0.1F * p_102529_);
         this.leftArm.xRot = this.rotlerpRad(this.swimAmount, this.leftArm.xRot, -2.5132742F) - this.swimAmount * 0.35F * Mth.sin(0.1F * p_102529_);
         this.rightArm.zRot = this.rotlerpRad(this.swimAmount, this.rightArm.zRot, -0.15F);
         this.leftArm.zRot = this.rotlerpRad(this.swimAmount, this.leftArm.zRot, 0.15F);
         this.leftLeg.xRot -= this.swimAmount * 0.55F * Mth.sin(0.1F * p_102529_);
         this.rightLeg.xRot += this.swimAmount * 0.55F * Mth.sin(0.1F * p_102529_);
         this.head.xRot = 0.0F;
      }

   }
}