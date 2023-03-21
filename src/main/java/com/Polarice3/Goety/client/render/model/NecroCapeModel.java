package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.utils.ModMathHelper;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ElytraItem;

public class NecroCapeModel<T extends LivingEntity> extends HumanoidModel<T> {
    private final ModelPart cape;
    private final ModelPart pants;
    private final ModelPart middle;

    public NecroCapeModel(ModelPart root) {
        super(root);
        this.cape = this.body.getChild("cape");
        this.pants = this.body.getChild("pants");
        this.middle = this.pants.getChild("middle");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION, 1.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition pants = body.addOrReplaceChild("pants", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition middle = pants.addOrReplaceChild("middle", CubeListBuilder.create().texOffs(60, 36).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.0F));

        PartDefinition cape = body.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(24, 64).addBox(-8.0F, 0.0F, -2.0F, 16.0F, 24.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(42, 16).addBox(-4.0F, -4.0F, -3.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(42, 16).mirror().addBox(-1.0F, -4.0F, -3.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 128);
    }

    public static LayerDefinition createHeadLayer(){
        MeshDefinition meshdefinition = HumanoidModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION, 1.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition pants = body.addOrReplaceChild("pants", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition middle = pants.addOrReplaceChild("middle", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition cape = body.addOrReplaceChild("cape", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition RightArm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 128);
    }

    @Override
    public void setupAnim(T pEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        super.setupAnim(pEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.bodyParts().forEach((modelPart -> modelPart.visible = !pEntity.isInvisible()));
        if (pEntity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem){
            this.rightArm.visible = false;
            this.leftArm.visible = false;
        }
        if (pEntity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem){
            this.cape.visible = false;
        } else {
            this.cape.visible = !pEntity.isInvisible();
        }
        if (!pEntity.getItemBySlot(EquipmentSlot.LEGS).isEmpty()){
            this.pants.visible = false;
        } else {
            this.pants.visible = !pEntity.isInvisible();
        }
        if (this.riding){
            this.pants.xRot = -1.4137167F;
        } else {
            this.pants.xRot = 0.0F;
        }
        float f = 1.0F;
        if (pEntity.getFallFlyingTicks() > 4) {
            f = (float)pEntity.getDeltaMovement().lengthSqr();
            f = f / 0.2F;
            f = f * f * f;
        }
        if (f < 1.0F) {
            f = 1.0F;
        }
        this.middle.xRot = Mth.abs(Mth.cos(limbSwing * 0.6662F) * 0.25F * limbSwingAmount / f);
        this.cape.xRot = ModMathHelper.modelDegrees(10.0F) + Mth.abs(Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount / f);
        if (pEntity.getVehicle() != null){
            if (pEntity.getVehicle().isAlive() && pEntity.getVehicle() instanceof LivingEntity livingEntity) {
                float f8 = Mth.lerp(Minecraft.getInstance().getPartialTick(), livingEntity.animationSpeedOld, livingEntity.animationSpeed);
                float f5 = livingEntity.animationPosition - livingEntity.animationSpeed * (1.0F - Minecraft.getInstance().getPartialTick());
                if (livingEntity.isBaby()) {
                    f5 *= 3.0F;
                }

                if (f8 > 1.0F) {
                    f8 = 1.0F;
                }
                this.cape.xRot = ModMathHelper.modelDegrees(45.0F) + Mth.abs(Mth.cos(f5 * 0.6662F) * 0.45F * f8 / f);
            }
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.bodyParts().forEach((modelPart -> modelPart.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)));
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.head, this.body, this.rightArm, this.leftArm);
    }
}
