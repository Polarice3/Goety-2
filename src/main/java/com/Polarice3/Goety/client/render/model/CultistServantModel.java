package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.ally.illager.cultist.CultistServant;
import com.Polarice3.Goety.utils.MathHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.*;

public class CultistServantModel<T extends CultistServant> extends HumanoidModel<T> {
    public ModelPart clothes;
    public ModelPart arms;
    public ModelPart all;
    protected final ModelPart nose;

    public CultistServantModel(ModelPart p_170677_) {
        super(p_170677_);
        this.all = p_170677_;
        this.clothes = p_170677_.getChild("clothes");
        this.arms = p_170677_.getChild("arms");
        this.nose = this.head.getChild("nose");
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(this.arms, this.clothes, this.all));
    }

    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.hat.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.hat.xRot = headPitch * ((float)Math.PI / 180F);
        this.arms.z = -1.0F;
        this.arms.xRot = -0.75F;
        if (this.riding) {
            this.rightArm.xRot = (-(float)Math.PI / 5F);
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = (-(float)Math.PI / 5F);
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.rightLeg.xRot = -1.4137167F;
            this.rightLeg.yRot = ((float)Math.PI / 10F);
            this.rightLeg.zRot = 0.07853982F;
            this.leftLeg.xRot = -1.4137167F;
            this.leftLeg.yRot = (-(float)Math.PI / 10F);
            this.leftLeg.zRot = -0.07853982F;
        } else {
            this.arms.y = 3.0F;
            this.rightArm.y = 2.0F;
            this.leftArm.y = 2.0F;
            this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
            this.rightLeg.yRot = 0.0F;
            this.rightLeg.zRot = 0.0F;
            this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
            this.leftLeg.yRot = 0.0F;
            this.leftLeg.zRot = 0.0F;
        }

        CultistServant.CultistServantArmPose cultistArmPose = entityIn.getArmPose();
        switch (cultistArmPose){
            case CROSSED:
                this.rightArm.xRot = 0;
                this.leftArm.xRot = 0;
                break;
            case ATTACKING:
                if (!entityIn.getMainHandItem().isEmpty() && !(entityIn.getMainHandItem().getItem() instanceof ProjectileWeaponItem)) {
                    AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, entityIn, this.attackTime, ageInTicks);
                }
                break;
            case BOMB_AND_WEAPON:
                if (!entityIn.getMainHandItem().isEmpty() && !(entityIn.getMainHandItem().getItem() instanceof ProjectileWeaponItem)) {
                    if (entityIn.getMainArm() == HumanoidArm.RIGHT) {
                        AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, entityIn, this.attackTime, ageInTicks);
                    } else {
                        AnimationUtils.swingWeaponDown(this.leftArm, this.rightArm, entityIn, this.attackTime, ageInTicks);
                    }
                }
                if (entityIn.getMainArm() == HumanoidArm.RIGHT){
                    this.leftArm.z = 0.0F;
                    this.leftArm.x = 5.0F;
                    this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                    this.leftArm.zRot = -2.3561945F;
                    this.leftArm.yRot = 0.0F;
                } else {
                    this.rightArm.z = 0.0F;
                    this.rightArm.x = -5.0F;
                    this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                    this.rightArm.zRot = 2.3561945F;
                    this.rightArm.yRot = 0.0F;
                }
                break;
            case BOW_AND_ARROW:
                this.rightArm.yRot = -0.1F + this.head.yRot;
                this.leftArm.yRot = 0.1F + this.head.yRot + 0.4F;
                this.rightArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
                this.leftArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
                break;
            case THROW_SPEAR:
                if (entityIn.getMainArm() == HumanoidArm.RIGHT) {
                    this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float)Math.PI;
                    this.rightArm.yRot = 0.0F;
                } else {
                    this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float)Math.PI;
                    this.leftArm.yRot = 0.0F;
                }
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entityIn, true);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
                break;
            case SPELLCASTING:
                this.rightArm.z = 0.0F;
                this.rightArm.x = -5.0F;
                this.leftArm.z = 0.0F;
                this.leftArm.x = 5.0F;
                this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                this.rightArm.zRot = 2.3561945F;
                this.leftArm.zRot = -2.3561945F;
                this.rightArm.yRot = 0.0F;
                this.leftArm.yRot = 0.0F;
                break;
            case PRAYING:
                this.rightArm.xRot = MathHelper.modelDegrees(125);
                this.leftArm.xRot = MathHelper.modelDegrees(125);
                this.head.xRot = MathHelper.modelDegrees(30);
                break;
            case SPELL_AND_WEAPON:
                if (!entityIn.getMainHandItem().isEmpty()) {
                    AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, entityIn, this.attackTime, ageInTicks);
                }
                if (entityIn.getMainArm() == HumanoidArm.RIGHT){
                    this.leftArm.z = 0.0F;
                    this.leftArm.x = 5.0F;
                    this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                    this.leftArm.zRot = -2.3561945F;
                    this.leftArm.yRot = 0.0F;
                } else {
                    this.rightArm.z = 0.0F;
                    this.rightArm.x = -5.0F;
                    this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                    this.rightArm.zRot = 2.3561945F;
                    this.rightArm.yRot = 0.0F;
                }
                break;
            case TORCH_AND_WEAPON:
                if (!entityIn.getMainHandItem().isEmpty() && !(entityIn.getMainHandItem().getItem() instanceof ProjectileWeaponItem)) {
                    if (entityIn.getMainArm() == HumanoidArm.RIGHT) {
                        AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, entityIn, this.attackTime, ageInTicks);
                    } else {
                        AnimationUtils.swingWeaponDown(this.leftArm, this.rightArm, entityIn, this.attackTime, ageInTicks);
                    }
                }
                if (entityIn.getMainArm() == HumanoidArm.RIGHT){
                    this.leftArm.xRot = -(2.0F + Mth.cos(ageInTicks * 0.09F) * 0.15F);
                } else {
                    this.rightArm.xRot = -(2.0F + Mth.cos(ageInTicks * 0.09F) * 0.15F);
                }
                break;
            case ITEM:
                this.head.xRot = 0.5F;
                this.head.yRot = 0.0F;
                this.hat.xRot = 0.5F;
                this.hat.yRot = 0.0F;
                if (entityIn.isLeftHanded()) {
                    this.rightArm.yRot = -0.5F;
                    this.rightArm.xRot = -0.9F;
                } else {
                    this.leftArm.yRot = 0.5F;
                    this.leftArm.xRot = -0.9F;
                }
                break;
            case DYING:
                if (entityIn.isDeadOrDying()){
                    this.rightArm.z = 0.0F;
                    this.rightArm.x = -5.0F;
                    this.leftArm.z = 0.0F;
                    this.leftArm.x = 5.0F;
                    this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                    this.leftArm.xRot = -Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                    this.rightArm.zRot = 2.3561945F;
                    this.leftArm.zRot = -2.3561945F;
                    this.rightLeg.xRot = 0.0F;
                    this.rightLeg.yRot = 0.0F;
                    this.rightLeg.zRot = 0.0F;
                    this.leftLeg.xRot = 0.0F;
                    this.leftLeg.yRot = 0.0F;
                    this.leftLeg.zRot = 0.0F;
                    this.head.xRot = -MathHelper.modelDegrees(25.0F);
                    this.hat.xRot = -MathHelper.modelDegrees(25.0F);
                }
        }

        if (this.leftArmPose == ArmPose.THROW_SPEAR) {
            this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float)Math.PI;
            this.leftArm.yRot = 0.0F;
        }

        if (this.rightArmPose == ArmPose.THROW_SPEAR) {
            this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float)Math.PI;
            this.rightArm.yRot = 0.0F;
        }

        boolean flag = cultistArmPose == CultistServant.CultistServantArmPose.CROSSED;
        this.arms.visible = flag;
        this.leftArm.visible = !flag;
        this.rightArm.visible = !flag;
        boolean flag2 = entityIn.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem
                || entityIn.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ArmorItem;
        this.clothes.visible = !flag2;
    }

    public boolean isAggressive(T entityIn) {
        return entityIn.isAggressive();
    }

    public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        this.rightArmPose = ArmPose.EMPTY;
        this.leftArmPose = ArmPose.EMPTY;
        if (entityIn.getMainArm() == HumanoidArm.RIGHT) {
            this.RightArmPoses(InteractionHand.MAIN_HAND, entityIn);
            this.LeftArmPoses(InteractionHand.OFF_HAND, entityIn);
        } else {
            this.RightArmPoses(InteractionHand.OFF_HAND, entityIn);
            this.LeftArmPoses(InteractionHand.MAIN_HAND, entityIn);
        }
        ItemStack itemstack = entityIn.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemstack.getItem() == Items.BOW && entityIn.isAggressive()) {
            if (entityIn.getMainArm() == HumanoidArm.RIGHT) {
                this.rightArmPose = ArmPose.BOW_AND_ARROW;
            } else {
                this.leftArmPose = ArmPose.BOW_AND_ARROW;
            }
        }
        if (itemstack.getItem() instanceof TridentItem && entityIn.isAggressive()) {
            if (entityIn.getMainArm() == HumanoidArm.RIGHT) {
                this.rightArmPose = ArmPose.THROW_SPEAR;
            } else {
                this.leftArmPose = ArmPose.THROW_SPEAR;
            }
        }
        super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
    }

    private void RightArmPoses (InteractionHand hand, T entityIn){
        ItemStack itemstack = entityIn.getItemInHand(hand);
        UseAnim useAction = itemstack.getUseAnimation();
        if (entityIn.getArmPose() != CultistServant.CultistServantArmPose.CROSSED){
            switch (useAction){
                case CROSSBOW:
                    this.rightArmPose = ArmPose.CROSSBOW_HOLD;
                    if (entityIn.isUsingItem()) {
                        this.rightArmPose = ArmPose.CROSSBOW_CHARGE;
                    }
                    break;
                case BOW:
                    this.rightArmPose = ArmPose.BOW_AND_ARROW;
                    break;
                case SPEAR:
                    this.rightArmPose = ArmPose.THROW_SPEAR;
                    break;
                default:
                    this.rightArmPose = ArmPose.EMPTY;
                    if (!itemstack.isEmpty()) {
                        this.rightArmPose = ArmPose.ITEM;
                    }
                    break;
            }
        }
    }

    private void LeftArmPoses (InteractionHand hand, T entityIn){
        ItemStack itemstack = entityIn.getItemInHand(hand);
        UseAnim useAction = itemstack.getUseAnimation();
        if (entityIn.getArmPose() != CultistServant.CultistServantArmPose.CROSSED){
            switch (useAction){
                case CROSSBOW:
                    this.leftArmPose = ArmPose.CROSSBOW_HOLD;
                    if (entityIn.isUsingItem()) {
                        this.leftArmPose = ArmPose.CROSSBOW_CHARGE;
                    }
                    break;
                case BOW:
                    this.leftArmPose = ArmPose.BOW_AND_ARROW;
                    break;
                case SPEAR:
                    this.leftArmPose = ArmPose.THROW_SPEAR;
                    break;
                default:
                    this.leftArmPose = ArmPose.EMPTY;
                    if (!itemstack.isEmpty()) {
                        this.leftArmPose = ArmPose.ITEM;
                    }
                    break;
            }
        }
    }

    private ModelPart getthisArm(HumanoidArm p_191216_1_) {
        return p_191216_1_ == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    public ModelPart func_205062_a() {
        return this.hat;
    }

    public ModelPart getHead() {
        return this.head;
    }

    public void translateToHand(HumanoidArm sideIn, PoseStack matrixStackIn) {
        this.getthisArm(sideIn).translateAndRotate(matrixStackIn);
    }

}
