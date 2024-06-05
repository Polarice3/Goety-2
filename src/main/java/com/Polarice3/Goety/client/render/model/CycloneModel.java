package com.Polarice3.Goety.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class CycloneModel<T extends Entity> extends HierarchicalModel<T> {
    private final ModelPart gust;
    private final ModelPart base;
    private final ModelPart bottom;
    private final ModelPart mid;
    private final ModelPart top;
    private final ModelPart wind;

    public CycloneModel(ModelPart root) {
        this.gust = root.getChild("gust");
        this.base = this.gust.getChild("base");
        this.bottom = this.base.getChild("bottom");
        this.mid = this.bottom.getChild("mid");
        this.top = this.mid.getChild("top");
        this.wind = this.gust.getChild("wind");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition gust = partdefinition.addOrReplaceChild("gust", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition base = gust.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.0F));

        PartDefinition bottom = base.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(65, 26).addBox(-4.0F, -3.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition mid = bottom.addOrReplaceChild("mid", CubeListBuilder.create().texOffs(65, 0).addBox(-6.0F, -3.0F, -6.0F, 12.0F, 6.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition top = mid.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 40).addBox(-8.0F, -3.0F, -8.0F, 16.0F, 6.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition wind = gust.addOrReplaceChild("wind", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -24.0F, -8.0F, 16.0F, 24.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    /**
     * Adapted from @lender544's Sandstorm model: <a href="https://github.com/lender544/L_ender-s-Cataclysm-Backport-1.19.2-1.80/blob/master/src/main/java/com/github/L_Ender/cataclysm/client/model/entity/Model_Sandstorm.java">...</a>
     */
    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart[] tornadoParts = new ModelPart[]{this.base, this.bottom, this.mid, this.top};
        this.chainFlap(tornadoParts, 0.25F, 0.1F, -2.0, ageInTicks, 1.0F);
        this.base.yRot = 0.0F;
        this.base.yRot += ageInTicks;
        ModelPart modelPart1 = this.bottom;
        modelPart1.yRot = 0.0F;
        modelPart1.yRot += -this.base.yRot + ageInTicks * 0.5F;
        modelPart1 = this.mid;
        modelPart1.yRot = 0.0F;
        modelPart1.yRot += -this.base.yRot - this.bottom.yRot + ageInTicks * 0.3F;
        modelPart1 = this.top;
        modelPart1.yRot = 0.0F;
        modelPart1.yRot += -this.base.yRot - this.bottom.yRot - this.mid.yRot + ageInTicks * 0.6F;
        this.wind.yRot = 0.0F;
        this.wind.yRot += ageInTicks * 0.25F;
    }

    public void chainFlap(ModelPart[] boxes, float speed, float degree, double rootOffset, float swing, float swingAmount) {
        float offset = this.calculateChainOffset(rootOffset, boxes);

        for(int index = 0; index < boxes.length; ++index) {
            boxes[index].zRot = 0.0F;
            boxes[index].zRot += this.calculateChainRotation(speed, degree, swing, swingAmount, offset, index);
        }

    }

    private float calculateChainRotation(float speed, float degree, float swing, float swingAmount, float offset, int boxIndex) {
        return Mth.cos(swing * speed + offset * (float)boxIndex) * swingAmount * degree;
    }

    private float calculateChainOffset(double rootOffset, ModelPart... boxes) {
        return (float)(rootOffset * Math.PI / (double)(2 * boxes.length));
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.gust.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return this.gust;
    }
}
