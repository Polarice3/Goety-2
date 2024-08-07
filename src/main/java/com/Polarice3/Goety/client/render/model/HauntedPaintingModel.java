package com.Polarice3.Goety.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class HauntedPaintingModel extends Model {
    private final ModelPart root;

    public HauntedPaintingModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.root = root;
    }

    public static LayerDefinition createLargeFrameLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(8, 4).addBox(-24.0F, -16.0F, -1.0F, 48.0F, 32.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition frame = bone.addOrReplaceChild("frame", CubeListBuilder.create().texOffs(0, 0).addBox(-24.0F, -18.0F, -1.0F, 48.0F, 2.0F, 2.0F, new CubeDeformation(-0.01F))
                .texOffs(0, 0).addBox(-24.0F, 16.0F, -1.0F, 48.0F, 2.0F, 2.0F, new CubeDeformation(-0.01F))
                .texOffs(0, 4).mirror().addBox(24.0F, -18.0F, -1.0F, 2.0F, 36.0F, 2.0F, new CubeDeformation(-0.01F)).mirror(false)
                .texOffs(0, 4).addBox(-26.0F, -18.0F, -1.0F, 2.0F, 36.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 0.0F, -0.5F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    public static LayerDefinition createMediumFrameLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(8, 4).addBox(-16.0F, -16.0F, -1.0F, 32.0F, 32.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 6.0F, 0.0F));

        PartDefinition frame = bone.addOrReplaceChild("frame", CubeListBuilder.create().texOffs(0, 0).addBox(-16.0F, -18.0F, -1.0F, 32.0F, 2.0F, 2.0F, new CubeDeformation(-0.01F))
                .texOffs(0, 0).addBox(-16.0F, 16.0F, -1.0F, 32.0F, 2.0F, 2.0F, new CubeDeformation(-0.01F))
                .texOffs(0, 4).mirror().addBox(16.0F, -18.0F, -1.0F, 2.0F, 36.0F, 2.0F, new CubeDeformation(-0.01F)).mirror(false)
                .texOffs(0, 4).addBox(-18.0F, -18.0F, -1.0F, 2.0F, 36.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 0.0F, -0.5F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    public static LayerDefinition createTallFrameLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(8, 4).addBox(-8.0F, -16.0F, -1.0F, 16.0F, 32.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition frame = bone.addOrReplaceChild("frame", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -18.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(-0.01F))
                .texOffs(0, 0).addBox(-8.0F, 16.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(-0.01F))
                .texOffs(0, 4).mirror().addBox(8.0F, -18.0F, -1.0F, 2.0F, 36.0F, 2.0F, new CubeDeformation(-0.01F)).mirror(false)
                .texOffs(0, 4).addBox(-10.0F, -18.0F, -1.0F, 2.0F, 36.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 0.0F, -0.5F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    public static LayerDefinition createWideFrameLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(8, 4).addBox(-16.0F, -8.0F, -1.0F, 32.0F, 16.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 14.0F, 0.0F));

        PartDefinition frame = bone.addOrReplaceChild("frame", CubeListBuilder.create().texOffs(0, 0).addBox(-16.0F, -10.0F, -1.0F, 32.0F, 2.0F, 2.0F, new CubeDeformation(-0.01F))
                .texOffs(0, 0).addBox(-16.0F, 8.0F, -1.0F, 32.0F, 2.0F, 2.0F, new CubeDeformation(-0.01F))
                .texOffs(0, 4).mirror().addBox(16.0F, -10.0F, -1.0F, 2.0F, 20.0F, 2.0F, new CubeDeformation(-0.01F)).mirror(false)
                .texOffs(0, 4).addBox(-18.0F, -10.0F, -1.0F, 2.0F, 20.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 0.0F, -0.5F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    public static LayerDefinition createSmallFrameLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(8, 4).addBox(-8.0F, -8.0F, -1.0F, 16.0F, 16.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, 0.0F));

        PartDefinition frame = bone.addOrReplaceChild("frame", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -10.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(-0.01F))
                .texOffs(0, 0).addBox(-8.0F, 8.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(-0.01F))
                .texOffs(0, 4).mirror().addBox(8.0F, -10.0F, -1.0F, 2.0F, 20.0F, 2.0F, new CubeDeformation(-0.01F)).mirror(false)
                .texOffs(0, 4).addBox(-10.0F, -10.0F, -1.0F, 2.0F, 20.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 0.0F, -0.5F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    public static LayerDefinition createLargeFramelessLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(8, 4).addBox(-24.0F, -16.0F, -1.0F, 48.0F, 32.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition frame = bone.addOrReplaceChild("frame", CubeListBuilder.create(), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    public static LayerDefinition createTallFramelessLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(8, 4).addBox(-8.0F, -16.0F, -1.0F, 16.0F, 32.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition frame = bone.addOrReplaceChild("frame", CubeListBuilder.create(), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    public void renderToBuffer(PoseStack p_103111_, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_) {
        this.root.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
    }
}
