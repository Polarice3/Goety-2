package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.api.entities.IShielded;
import com.Polarice3.Goety.client.render.animation.RoyalGuardAnimations;
import com.Polarice3.Goety.common.entities.ally.illager.RoyalGuardServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieRoyalGuardServant;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Mob;

public class RoyalGuardModel<T extends Mob & IShielded> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart guard;
    private final ModelPart body;
    private final ModelPart left_arm;
    private final ModelPart shield;
    private final ModelPart head;

    public RoyalGuardModel(ModelPart root) {
        this.root = root;
        this.guard = root.getChild("guard");
        this.body = this.guard.getChild("body");
        this.left_arm = this.body.getChild("left_arm");
        this.shield = this.left_arm.getChild("shield");
        this.head = this.body.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition guard = partdefinition.addOrReplaceChild("guard", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition right_leg = guard.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 18).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(34, 50).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.3F))
                .texOffs(46, 46).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(-3.5F, 0.0F, 1.5F, 0.0873F, 0.6109F, 0.0436F));

        PartDefinition cube_r1 = right_leg.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(48, 0).mirror().addBox(-2.0F, 0.0F, -2.5F, 4.0F, 6.0F, 6.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.0F, -0.5F, 0.0F, 0.0F, 0.3927F));

        PartDefinition right_boot = right_leg.addOrReplaceChild("right_boot", CubeListBuilder.create().texOffs(60, 15).addBox(-3.4F, -6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(1.4F, 12.0F, 0.0F));

        PartDefinition left_leg = guard.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 18).mirror().addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(46, 46).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.5F))
                .texOffs(34, 50).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.3F)).mirror(false), PartPose.offsetAndRotation(2.9F, 0.0F, -3.0F, -0.0873F, -0.3491F, 0.0F));

        PartDefinition cube_r2 = left_leg.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 6.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.1F, -1.0F, -0.5F, 0.0F, 0.0F, -0.3927F));

        PartDefinition left_boot = left_leg.addOrReplaceChild("left_boot", CubeListBuilder.create().texOffs(60, 15).addBox(0.75F, -6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-2.65F, 12.0F, 0.0F));

        PartDefinition body = guard.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 18).addBox(-4.0F, -12.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(111, 57).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition belt = body.addOrReplaceChild("belt", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -3.5F));

        PartDefinition cube_r3 = belt.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(120, 56).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0436F, 0.0F, 0.0F));

        PartDefinition armor = body.addOrReplaceChild("armor", CubeListBuilder.create().texOffs(0, 46).addBox(-2.5F, 0.0F, 0.0F, 10.0F, 12.0F, 6.0F, new CubeDeformation(0.01F))
                .texOffs(78, 46).addBox(-2.5F, 0.0F, 0.0F, 10.0F, 12.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(-2.5F, -12.0F, -3.0F));

        PartDefinition upperBody_r1 = armor.addOrReplaceChild("upperBody_r1", CubeListBuilder.create().texOffs(56, 55).addBox(-3.0F, 1.0F, -0.5F, 5.0F, 4.0F, 3.0F, new CubeDeformation(0.01F))
                .texOffs(56, 55).mirror().addBox(2.5F, 1.0F, -0.5F, 5.0F, 4.0F, 3.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(0.25F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(112, 13).addBox(-2.75F, 6.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.26F))
                .texOffs(114, 47).addBox(-3.75F, 4.0F, -2.0F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-5.25F, -10.0F, 0.5F, 0.48F, 0.6981F, 0.6981F));

        PartDefinition right_gauntlet = right_arm.addOrReplaceChild("right_gauntlet", CubeListBuilder.create().texOffs(112, 19).mirror().addBox(-4.0F, -1.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.4F)).mirror(false)
                .texOffs(112, 27).addBox(-4.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(1.0F, -1.0F, 0.0F));

        PartDefinition cube_r4 = right_gauntlet.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(89, 24).mirror().addBox(-0.5F, -3.0F, -3.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(-4.5F, 0.0F, 0.5F, 0.0F, 0.0F, -0.4363F));

        PartDefinition rightItem = right_arm.addOrReplaceChild("rightItem", CubeListBuilder.create(), PartPose.offset(-1.0F, 7.0F, 1.0F));

        PartDefinition mace = rightItem.addOrReplaceChild("mace", CubeListBuilder.create().texOffs(0, 38).addBox(-9.0F, 7.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(5, 36).addBox(-11.0F, -4.0F, -7.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(5, 36).addBox(-9.0F, -6.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 0.0F, -7.0F, 0.8802F, 0.1119F, 0.1343F));

        PartDefinition mace_r1 = mace.addOrReplaceChild("mace_r1", CubeListBuilder.create().texOffs(5, 36).addBox(5.0F, -5.0F, 12.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, 0.0F, 1.5708F, -1.5708F));

        PartDefinition mace_r2 = mace.addOrReplaceChild("mace_r2", CubeListBuilder.create().texOffs(5, 36).addBox(-1.0F, 8.0F, 5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition mace_r3 = mace.addOrReplaceChild("mace_r3", CubeListBuilder.create().texOffs(5, 36).addBox(-1.0F, -10.0F, 12.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 36).addBox(-1.0F, 0.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 34).addBox(-1.0F, -4.0F, 9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 34).addBox(-1.0F, -3.0F, 8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 34).addBox(-1.0F, -2.0F, 7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 38).addBox(-1.0F, -1.0F, 6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 36).addBox(-1.0F, 1.0F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition mace_r4 = mace.addOrReplaceChild("mace_r4", CubeListBuilder.create().texOffs(5, 36).addBox(-1.0F, 1.0F, -13.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition mace_r5 = mace.addOrReplaceChild("mace_r5", CubeListBuilder.create().texOffs(5, 36).addBox(-6.0F, -4.0F, 12.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, 0.0F, -1.5708F, 1.5708F));

        PartDefinition mace_r6 = mace.addOrReplaceChild("mace_r6", CubeListBuilder.create().texOffs(25, 38).addBox(-4.0F, -11.0F, 0.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(25, 38).addBox(-4.0F, -11.0F, -1.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition mace_r7 = mace.addOrReplaceChild("mace_r7", CubeListBuilder.create().texOffs(0, 34).addBox(0.0F, -10.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 34).addBox(0.0F, -9.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 34).addBox(0.0F, -8.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 10.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        PartDefinition mace_r8 = mace.addOrReplaceChild("mace_r8", CubeListBuilder.create().texOffs(0, 36).addBox(0.0F, -8.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 36).addBox(0.0F, -6.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 36).addBox(0.0F, -7.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 38).addBox(0.0F, -4.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 38).addBox(0.0F, -5.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, -3.1416F, 0.0F, 3.1416F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 0).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(112, 13).mirror().addBox(-1.25F, 6.0F, -2.25F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.26F)).mirror(false)
                .texOffs(114, 47).mirror().addBox(0.75F, 4.0F, -2.25F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(4.0F, -11.0F, 0.5F, -1.0472F, 0.0F, 0.0F));

        PartDefinition left_gauntlet = left_arm.addOrReplaceChild("left_gauntlet", CubeListBuilder.create().texOffs(112, 19).addBox(0.25F, -1.0F, -2.25F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.4F))
                .texOffs(112, 27).mirror().addBox(0.25F, 0.0F, -2.25F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.3F)).mirror(false), PartPose.offset(-1.25F, -1.0F, 0.0F));

        PartDefinition cube_r5 = left_gauntlet.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(89, 24).addBox(-4.5F, -3.0F, -3.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(4.75F, 0.0F, 0.25F, 0.0F, 0.0F, 0.4363F));

        PartDefinition shield = left_arm.addOrReplaceChild("shield", CubeListBuilder.create().texOffs(80, 0).addBox(-7.0F, -10.5F, -1.0F, 14.0F, 22.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(85, 38).addBox(-7.5F, -11.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.25F))
                .texOffs(91, 38).addBox(5.5F, -11.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.25F))
                .texOffs(85, 41).addBox(-7.5F, 10.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.25F))
                .texOffs(91, 41).addBox(5.5F, 10.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.0F, 10.5F, -1.0F, 1.0472F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition helmet = head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(47, 26).addBox(-5.0F, -35.5F, -4.5F, 10.0F, 10.0F, 9.0F, new CubeDeformation(0.25F))
                .texOffs(116, 9).addBox(-2.0F, -36.85F, -4.65F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.1F))
                .texOffs(118, 1).mirror().addBox(5.25F, -37.7F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.15F)).mirror(false)
                .texOffs(118, 1).addBox(-7.25F, -37.7F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.15F)), PartPose.offset(0.0F, 25.5F, 0.0F));

        PartDefinition right_arm2 = body.addOrReplaceChild("right_arm2", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, -11.0F, 1.5F, -1.0472F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (!entity.isDeadOrDying()){
            this.animateHeadLookTarget(netHeadYaw, headPitch);
        }
        if (entity instanceof ZombieRoyalGuardServant blackguard) {
            this.animate(blackguard.idleAnimationState, RoyalGuardAnimations.IDLE, ageInTicks);
            this.animate(blackguard.standAnimationState, RoyalGuardAnimations.STAND, ageInTicks);
            this.animate(blackguard.attackAnimationState, RoyalGuardAnimations.ATTACK, ageInTicks);
        } else if (entity instanceof RoyalGuardServant royalGuard) {
            this.animate(royalGuard.idleAnimationState, RoyalGuardAnimations.IDLE, ageInTicks);
            this.animate(royalGuard.standAnimationState, RoyalGuardAnimations.STAND, ageInTicks);
            this.animate(royalGuard.attackAnimationState, RoyalGuardAnimations.ATTACK, ageInTicks);
        }
        this.animateWalk(RoyalGuardAnimations.WALK, limbSwing, limbSwingAmount, 1.5F, 2.5F);
        this.shield.visible = entity.hasShield();
    }

    private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
