package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.SculpturedStatueBlock;
import com.Polarice3.Goety.common.blocks.entities.SculpturedStatueBlockEntity;
import com.Polarice3.Goety.utils.MathHelper;
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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SculpturedStatueRenderer implements BlockEntityRenderer<SculpturedStatueBlockEntity> {
    public static final ResourceLocation TEXTURE = Goety.location("textures/entity/statue/sculptured/0.png");
    private final ModelPart statue;
    private final ModelPart statue_slim;
    private ModelPart head;
    private ModelPart body;
    private ModelPart right_arm;
    private ModelPart left_arm;
    private ModelPart right_leg;
    private ModelPart left_leg;

    public SculpturedStatueRenderer(BlockEntityRendererProvider.Context ctx) {
        ModelPart modelPart = ctx.bakeLayer(ModBlockLayer.SCULPTURED_STATUE);
        this.statue = modelPart.getChild("statue");
        ModelPart modelPart2 = ctx.bakeLayer(ModBlockLayer.SCULPTURED_STATUE_SLIM);
        this.statue_slim = modelPart2.getChild("statue");
        this.bindParts(this.statue);
    }

    private void bindParts(ModelPart root) {
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.right_arm = root.getChild("right_arm");
        this.left_arm = root.getChild("left_arm");
        this.right_leg = root.getChild("right_leg");
        this.left_leg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition statue = partdefinition.addOrReplaceChild("statue", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition Head = statue.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -24.0F, 0.0F));

        PartDefinition Body = statue.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -24.0F, 0.0F));

        PartDefinition RightArm = statue.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, -22.0F, 0.0F));

        PartDefinition LeftArm = statue.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(5.0F, -22.0F, 0.0F));

        PartDefinition RightLeg = statue.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-1.9F, -12.0F, 0.0F));

        PartDefinition LeftLeg = statue.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(1.9F, -12.0F, 0.0F));

        PartDefinition base = statue.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 64).addBox(-7.0F, -2.0F, -7.0F, 14.0F, 2.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(0, 80).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 4.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 89).addBox(-2.0F, -4.0F, -7.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 128);
    }

    public static LayerDefinition createSlimLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition statue = partdefinition.addOrReplaceChild("statue", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition Head = statue.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -24.0F, 0.0F));

        PartDefinition Body = statue.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -24.0F, 0.0F));

        PartDefinition RightArm = statue.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(40, 32).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, -22.0F, 0.0F));

        PartDefinition LeftArm = statue.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(5.0F, -22.0F, 0.0F));

        PartDefinition RightLeg = statue.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-1.9F, -12.0F, 0.0F));

        PartDefinition LeftLeg = statue.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(1.9F, -12.0F, 0.0F));

        PartDefinition base = statue.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 64).addBox(-7.0F, -2.0F, -7.0F, 14.0F, 2.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(0, 80).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 4.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 89).addBox(-2.0F, -4.0F, -7.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 128);
    }

    public void basePose() {
        this.head.xRot = 0.0F;
        this.head.yRot = 0.0F;
        this.head.zRot = 0.0F;
        this.body.xRot = 0.0F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;
        this.right_arm.xRot = 0.0F;
        this.right_arm.yRot = 0.0F;
        this.right_arm.zRot = 0.0F;
        this.left_arm.xRot = 0.0F;
        this.left_arm.yRot = 0.0F;
        this.left_arm.zRot = 0.0F;
        this.right_leg.xRot = 0.0F;
        this.right_leg.yRot = 0.0F;
        this.right_leg.zRot = 0.0F;
        this.left_leg.xRot = 0.0F;
        this.left_leg.yRot = 0.0F;
        this.left_leg.zRot = 0.0F;
    }

    public void firstPose() {
        this.head.xRot = MathHelper.modelDegrees(-6);
        this.head.yRot = MathHelper.modelDegrees(5);
        this.head.zRot = 0.0F;
        this.body.xRot = 0.0F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;
        this.right_arm.xRot = MathHelper.modelDegrees(-10);
        this.right_arm.yRot = 0.0F;
        this.right_arm.zRot = 0.0F;
        this.left_arm.xRot = MathHelper.modelDegrees(12);
        this.left_arm.yRot = 0.0F;
        this.left_arm.zRot = 0.0F;
        this.right_leg.xRot = MathHelper.modelDegrees(11);
        this.right_leg.yRot = 0.0F;
        this.right_leg.zRot = MathHelper.modelDegrees(2);
        this.left_leg.xRot = MathHelper.modelDegrees(-10);
        this.left_leg.yRot = 0.0F;
        this.left_leg.zRot = MathHelper.modelDegrees(-2);
    }

    public void secondPose() {
        this.head.xRot = MathHelper.modelDegrees(-20);
        this.head.yRot = 0.0F;
        this.head.zRot = 0.0F;
        this.body.xRot = 0.0F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;
        this.right_arm.xRot = 0.25F;
        this.right_arm.yRot = 0.0F;
        this.right_arm.zRot = 2.3561945F;
        this.left_arm.xRot = 0.25F;
        this.left_arm.yRot = 0.0F;
        this.left_arm.zRot = -2.3561945F;
        this.right_leg.xRot = 0.0F;
        this.right_leg.yRot = 0.0F;
        this.right_leg.zRot = 0.0F;
        this.left_leg.xRot = 0.0F;
        this.left_leg.yRot = 0.0F;
        this.left_leg.zRot = 0.0F;
    }

    public void thirdPose() {
        this.head.xRot = MathHelper.modelDegrees(20);
        this.head.yRot = 0.0F;
        this.head.zRot = 0.0F;
        this.body.xRot = 0.0F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;
        this.right_arm.xRot = MathHelper.modelDegrees(-50);
        this.right_arm.yRot = MathHelper.modelDegrees(-25);
        this.right_arm.zRot = MathHelper.modelDegrees(-15);
        this.left_arm.xRot = MathHelper.modelDegrees(-50);
        this.left_arm.yRot = MathHelper.modelDegrees(25);
        this.left_arm.zRot = MathHelper.modelDegrees(15);
        this.right_leg.xRot = 0.0F;
        this.right_leg.yRot = 0.0F;
        this.right_leg.zRot = 0.0F;
        this.left_leg.xRot = 0.0F;
        this.left_leg.yRot = 0.0F;
        this.left_leg.zRot = 0.0F;
    }

    @Override
    public void render(@Nullable SculpturedStatueBlockEntity pBlockEntity, float pPartialTicks, PoseStack pStack, MultiBufferSource pBuffer, int pLight, int pOverlay) {
        boolean slim = pBlockEntity != null && pBlockEntity.getBlockState().getBlock() instanceof SculpturedStatueBlock statueBlock && statueBlock.isSlim();
        ModelPart activeRoot = slim ? this.statue_slim : this.statue;
        this.bindParts(activeRoot);
        pStack.pushPose();
        pStack.scale(-1.0F, -1.0F, 1.0F);
        pStack.translate(-0.5D, 0.0D, 0.5D);
        if (pBlockEntity != null) {
            Direction facing = pBlockEntity.getBlockState().getValue(SculpturedStatueBlock.FACING);
            switch (facing) {
                case SOUTH:
                default:
                    break;
                case WEST:
                    pStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                    break;
                case EAST:
                    pStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
                    break;
                case NORTH:
                    pStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            }
        }

        pStack.pushPose();
        RenderType layer = RenderType.entityCutoutNoCullZOffset(TEXTURE);
        if (pBlockEntity != null) {
            BlockState blockState = pBlockEntity.getBlockState();
            layer = RenderType.entityCutoutNoCullZOffset(getTexture(blockState));
            int pose = blockState.getValue(SculpturedStatueBlock.POSE);
            switch (pose) {
                case 0:
                    this.basePose();
                    break;
                case 1:
                    this.firstPose();
                    break;
                case 2:
                    this.secondPose();
                    break;
                case 3:
                    this.thirdPose();
                    break;
            }
        } else {
            this.basePose();
        }
        VertexConsumer vertexConsumer = pBuffer.getBuffer(layer);
        activeRoot.render(pStack, vertexConsumer, pLight, pOverlay);
        pStack.popPose();

        pStack.popPose();
    }

    public void renderItem(BlockState blockState, float rotateY, PoseStack pStack, MultiBufferSource pBuffer, int pCombinedLight) {
        boolean slim = blockState != null && blockState.getBlock() instanceof SculpturedStatueBlock statueBlock && statueBlock.isSlim();
        ModelPart activeRoot = slim ? this.statue_slim : this.statue;
        this.bindParts(activeRoot);
        pStack.pushPose();
        pStack.translate(0.5D, 0.0D, 0.5D);

        pStack.scale(-1.0F, -1.0F, 1.0F);
        pStack.scale(0.25F, 0.25F, 0.25F);
        pStack.pushPose();
        RenderType layer = RenderType.entityCutoutNoCullZOffset(TEXTURE);
        if (blockState != null) {
            layer = RenderType.entityCutoutNoCullZOffset(getTexture(blockState));
        }
        VertexConsumer vertexConsumer = pBuffer.getBuffer(layer);
        activeRoot.yRot = rotateY * ((float)Math.PI / 180F);
        this.basePose();
        activeRoot.render(pStack, vertexConsumer, pCombinedLight, OverlayTexture.NO_OVERLAY);
        pStack.popPose();

        pStack.popPose();
    }

    public static ResourceLocation getTexture(BlockState blockState) {
        ResourceLocation texture = TEXTURE;
        if (blockState.getBlock() instanceof SculpturedStatueBlock statueBlock) {
            texture = Goety.location("textures/entity/statue/sculptured/" + statueBlock.getStatueType() + ".png");
        }
        return texture;
    }

    public boolean shouldRenderOffScreen(SculpturedStatueBlockEntity p_112138_) {
        return true;
    }

    public boolean shouldRender(SculpturedStatueBlockEntity p_173531_, Vec3 p_173532_) {
        return Vec3.atCenterOf(p_173531_.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(p_173532_.multiply(1.0D, 0.0D, 1.0D), (double)this.getViewDistance());
    }
}
