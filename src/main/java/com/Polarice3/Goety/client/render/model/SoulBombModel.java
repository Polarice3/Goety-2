package com.Polarice3.Goety.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class SoulBombModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart bomb;

    public SoulBombModel(ModelPart root) {
        this.bomb = root.getChild("bomb");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bomb = partdefinition.addOrReplaceChild("bomb", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -9.0F, -5.0F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    public void setupAnim(float p_103812_, float p_103813_) {
        this.bomb.yRot = p_103812_ * ((float)Math.PI / 180F);
        this.bomb.xRot = p_103813_ * ((float)Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bomb.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

}
