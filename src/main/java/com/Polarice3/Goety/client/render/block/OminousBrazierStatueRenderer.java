package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.OminousBrazierStatueBlock;
import com.Polarice3.Goety.common.blocks.OminousStatueBlock;
import com.Polarice3.Goety.common.blocks.entities.OminousBrazierStatueBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class OminousBrazierStatueRenderer implements BlockEntityRenderer<OminousBrazierStatueBlockEntity> {
    public static final ResourceLocation TEXTURE = Goety.location("textures/entity/statue/brazier_statue.png");
    public static final ResourceLocation LIT = Goety.location("textures/entity/statue/brazier_statue_lit.png");
    private final ModelPart statue;

    public OminousBrazierStatueRenderer(BlockEntityRendererProvider.Context ctx) {
        ModelPart modelpart = ctx.bakeLayer(ModBlockLayer.BRAZIER_STATUE);
        this.statue = modelpart.getChild("statue");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition statue = partdefinition.addOrReplaceChild("statue", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = statue.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -12.0F, -5.0F, 10.0F, 12.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(29, 0).addBox(-2.0F, -15.0F, -5.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 1.0F));

        PartDefinition right_horn = head.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(29, 5).addBox(5.0F, -2.0F, -2.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(39, 4).addBox(7.0F, -5.0F, -2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition left_horn = head.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(29, 5).addBox(-8.0F, -2.0F, -2.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(45, 4).addBox(-8.0F, -5.0F, -2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -5.0F));

        PartDefinition body = statue.addOrReplaceChild("body", CubeListBuilder.create().texOffs(32, 21).addBox(-4.0F, -2.0F, -4.0F, 8.0F, 13.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offset(0.0F, -23.0F, 2.0F));

        PartDefinition armor = body.addOrReplaceChild("armor", CubeListBuilder.create().texOffs(0, 21).addBox(-9.0F, -13.0F, -1.0F, 10.0F, 13.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 12.0F, -3.0F));

        PartDefinition upperBody_r1 = armor.addOrReplaceChild("upperBody_r1", CubeListBuilder.create().texOffs(16, 57).addBox(3.0F, 1.2F, -0.5F, 5.0F, 4.0F, 3.0F, new CubeDeformation(-0.05F))
                .texOffs(16, 57).mirror().addBox(-2.5F, 1.2F, -0.5F, 5.0F, 4.0F, 3.0F, new CubeDeformation(-0.05F)).mirror(false), PartPose.offsetAndRotation(-6.75F, -13.0F, -1.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition skirt = body.addOrReplaceChild("skirt", CubeListBuilder.create().texOffs(0, 40).addBox(-5.0F, 0.0F, -2.5F, 10.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, -1.7F, 0.4363F, 0.0F, 0.0F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.0F, 0.0F, -4.0F, -1.5708F, 0.5672F, 1.5708F));

        PartDefinition right_shoulder = right_arm.addOrReplaceChild("right_shoulder", CubeListBuilder.create().texOffs(22, 75).addBox(-4.5F, 0.5F, -2.25F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(2.0F, 0.0F, 0.0F));

        PartDefinition rightArm_r1 = right_shoulder.addOrReplaceChild("rightArm_r1", CubeListBuilder.create().texOffs(0, 70).addBox(5.0F, -2.5F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 79).addBox(1.0F, -3.5F, -3.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        PartDefinition right_gauntlet = right_arm.addOrReplaceChild("right_gauntlet", CubeListBuilder.create().texOffs(6, 66).addBox(-4.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offset(2.0F, -2.0F, 0.0F));

        PartDefinition rightArm_r2 = right_gauntlet.addOrReplaceChild("rightArm_r2", CubeListBuilder.create().texOffs(36, 64).addBox(-3.0F, 6.5F, -5.0F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition rightArm_r3 = right_gauntlet.addOrReplaceChild("rightArm_r3", CubeListBuilder.create().texOffs(36, 74).mirror().addBox(-3.0F, -1.0F, -1.5F, 6.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.5F, 11.5F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(6.0F, 1.0F, -2.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition left_shoulder = left_arm.addOrReplaceChild("left_shoulder", CubeListBuilder.create().texOffs(22, 64).addBox(5.0F, -25.5F, -3.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 70).addBox(9.0F, -24.5F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 75).addBox(5.5F, -21.5F, -2.25F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(-6.0F, 22.0F, 0.0F));

        PartDefinition left_gauntlet = left_arm.addOrReplaceChild("left_gauntlet", CubeListBuilder.create().texOffs(6, 66).mirror().addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(-0.01F)).mirror(false), PartPose.offset(-2.0F, -1.0F, 0.0F));

        PartDefinition leftArm_r1 = left_gauntlet.addOrReplaceChild("leftArm_r1", CubeListBuilder.create().texOffs(36, 74).addBox(-3.0F, -1.0F, -1.5F, 6.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 10.5F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition leftArm_r2 = left_gauntlet.addOrReplaceChild("leftArm_r2", CubeListBuilder.create().texOffs(36, 64).mirror().addBox(-3.0F, -1.5F, -1.0F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.0F, 8.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition right_legs = statue.addOrReplaceChild("right_legs", CubeListBuilder.create().texOffs(48, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(32, 55).addBox(-2.0F, 6.6F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.5F))
                .texOffs(32, 52).addBox(-2.0F, 4.25F, -3.25F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -12.0F, 1.0F));

        PartDefinition left_legs = statue.addOrReplaceChild("left_legs", CubeListBuilder.create().texOffs(48, 48).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(32, 55).addBox(-2.0F, 6.6F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.5F))
                .texOffs(32, 52).addBox(-2.0F, 4.25F, -3.25F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -12.0F, 1.0F));

        PartDefinition brazier = statue.addOrReplaceChild("brazier", CubeListBuilder.create().texOffs(0, 82).addBox(-13.5F, -25.5F, -9.0F, 12.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition bowl = brazier.addOrReplaceChild("bowl", CubeListBuilder.create().texOffs(0, 94).addBox(-6.0F, 6.0F, -6.0F, 12.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(28, 94).addBox(-6.0F, 6.0F, 4.0F, 12.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 99).addBox(4.0F, 6.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(20, 99).addBox(-6.0F, 6.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 110).addBox(-4.0F, 9.0F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(32, 110).addBox(-4.0F, 8.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.5F, -19.5F, -9.0F));

        return LayerDefinition.create(meshdefinition, 64, 128);
    }

    @Override
    public void render(@Nullable OminousBrazierStatueBlockEntity pBlockEntity, float pPartialTicks, PoseStack pStack, MultiBufferSource pBuffer, int pLight, int pOverlay) {
        pStack.pushPose();
        pStack.scale(-1.0F, -1.0F, 1.0F);
        pStack.translate(-0.5D, 0.0D, 0.5D);
        if (pBlockEntity != null) {
            Direction facing = pBlockEntity.getBlockState().getValue(OminousStatueBlock.FACING);
            switch (facing) {
                case NORTH:
                default:
                    break;
                case EAST:
                    pStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                    break;
                case WEST:
                    pStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
                    break;
                case SOUTH:
                    pStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            }
        }

        pStack.pushPose();
        RenderType layer = RenderType.entityCutoutNoCullZOffset(TEXTURE);
        if (pBlockEntity != null) {
            if (pBlockEntity.getBlockState().getValue(OminousBrazierStatueBlock.LIT)) {
                layer = RenderType.entityCutoutNoCullZOffset(LIT);
            }
        }
        VertexConsumer vertexConsumer = pBuffer.getBuffer(layer);
        this.statue.render(pStack, vertexConsumer, pLight, pOverlay);
        pStack.popPose();

        pStack.popPose();
    }

    public boolean shouldRenderOffScreen(OminousBrazierStatueBlockEntity p_112138_) {
        return true;
    }

    public boolean shouldRender(OminousBrazierStatueBlockEntity p_173531_, Vec3 p_173532_) {
        return Vec3.atCenterOf(p_173531_.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(p_173532_.multiply(1.0D, 0.0D, 1.0D), (double)this.getViewDistance());
    }
}
