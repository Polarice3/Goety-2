package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.config.ItemConfig;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class DarkRobeModel extends HumanoidModel<LivingEntity> {
    private final ModelPart cape;

    public DarkRobeModel(ModelPart root) {
        super(root);
        this.cape = root.getChild("cape");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION, 0.5F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 24.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.75F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(40, 58).addBox(-4.0F, -2.71F, 0.63F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.75F)), PartPose.offsetAndRotation(0.0F, -5.5F, 3.5F, -0.3927F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(24, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

        partdefinition.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(44, 15).addBox(-4.5F, 0.0F, -1.0F, 9.0F, 20.0F, 1.0F, new CubeDeformation(0.0F), 1.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.bodyParts().forEach((modelPart -> modelPart.visible = !entityIn.isInvisible()));
        this.head.visible = ItemConfig.ShowRobeHoods.get();
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.bodyParts().forEach((modelPart -> modelPart.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)));
    }

    public void renderCape(PoseStack pMatrixStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay) {
        this.cape.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.head, this.body, this.rightArm, this.leftArm);
    }
}
