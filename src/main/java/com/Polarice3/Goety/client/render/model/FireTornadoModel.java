package com.Polarice3.Goety.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class FireTornadoModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart Gust;
    private final ModelPart base;
    private final ModelPart gust1;
    private final ModelPart gust2;
    private final ModelPart gust3;
    private final ModelPart wind;

    public FireTornadoModel(ModelPart root) {
        this.Gust = root.getChild("Gust");
        this.base = this.Gust.getChild("base");
        this.gust1 = this.Gust.getChild("gust1");
        this.gust2 = this.Gust.getChild("gust2");
        this.gust3 = this.Gust.getChild("gust3");
        this.wind = this.Gust.getChild("wind");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Gust = partdefinition.addOrReplaceChild("Gust", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition base = Gust.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition gust1 = Gust.addOrReplaceChild("gust1", CubeListBuilder.create().texOffs(65, 26).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

        PartDefinition gust2 = Gust.addOrReplaceChild("gust2", CubeListBuilder.create().texOffs(65, 0).addBox(-6.0F, -4.0F, -6.0F, 12.0F, 6.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.0F, 0.0F));

        PartDefinition gust3 = Gust.addOrReplaceChild("gust3", CubeListBuilder.create().texOffs(0, 40).addBox(-8.0F, -9.0F, -8.0F, 16.0F, 6.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.0F, 0.0F));

        PartDefinition wind = Gust.addOrReplaceChild("wind", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -24.0F, -8.0F, 16.0F, 24.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        base.yRot -= ageInTicks;
        gust1.yRot -= ageInTicks * 1.25F;
        gust2.yRot -= ageInTicks * 1.2F;
        gust3.yRot -= ageInTicks * 1.27F;
        wind.yRot += ageInTicks * 0.25F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Gust.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
