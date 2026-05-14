package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.HeresiarchModel;
import com.Polarice3.Goety.common.entities.hostile.cultists.Heresiarch;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.ModelPartPose;
import com.Polarice3.Goety.utils.ModelSnapshot;
import com.Polarice3.Goety.utils.ModelUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Map;

public class HeresiarchRenderer<T extends Heresiarch> extends MobRenderer<T, HeresiarchModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/cultist/heresiarch.png");
    private static final ResourceLocation RUNE = Goety.location("textures/entity/cultist/spell_rune.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(RUNE);

    private static final RenderType AFTERIMAGE_RENDER_TYPE = ModRenderType.entityTranslucentNoDepth(TEXTURE);

    private static final float SNAPSHOT_INTERVAL = 10.0F;
    private static final float SNAPSHOT_LIFESPAN = 20.0F;
    private final HeresiarchModel<T> shadowModel;

    public HeresiarchRenderer(EntityRendererProvider.Context p_174304_) {
        super(p_174304_, new HeresiarchModel<>(p_174304_.bakeLayer(ModModelLayer.HERESIARCH)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, p_174304_.getItemInHandRenderer()) {
            public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T heresiarch, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                if (heresiarch.getMainHandItem().is(ModTags.Items.WITCH_CURRENCY)) {
                    super.render(matrixStackIn, bufferIn, packedLightIn, heresiarch, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                }

            }
        });
        this.shadowModel = new HeresiarchModel<>(p_174304_.bakeLayer(ModModelLayer.HERESIARCH_SHADOW));
    }

    protected void scale(T entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.25F, 1.25F, 1.25F);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
        float age = entity.tickCount + partialTicks;
        poseStack.pushPose();
        float runeScale = Mth.lerp(partialTicks, entity.prevRuneScale, entity.runeScale) * 2.0F;
        poseStack.translate(0.0D, entity.getBbHeight() * 1.65F, 0.0D);
        poseStack.scale(runeScale, runeScale, runeScale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(age));
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
        vertex(vertexconsumer, matrix4f, matrix3f, LightTexture.FULL_BRIGHT, 0.0F, 0, 0, 1);
        vertex(vertexconsumer, matrix4f, matrix3f, LightTexture.FULL_BRIGHT, 1.0F, 0, 1, 1);
        vertex(vertexconsumer, matrix4f, matrix3f, LightTexture.FULL_BRIGHT, 1.0F, 1, 1, 0);
        vertex(vertexconsumer, matrix4f, matrix3f, LightTexture.FULL_BRIGHT, 0.0F, 1, 0, 0);
        poseStack.popPose();
        if (entity.isAlive()) {
            double currentX = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double currentY = Mth.lerp(partialTicks, entity.yo, entity.getY());
            double currentZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            float currentTick = getBob(entity, partialTicks);
            if (entity.trailSnapshots.isEmpty() || currentTick - entity.lastTrailTick > SNAPSHOT_INTERVAL) {
                if (entity.shouldAddTrailSnapshot()) {
                    Map<String, ModelPartPose> snapshot = ModelUtil.saveModelSnapshot(this.getModel().allPartNames, this.getModel()::getAnyDescendantWithName);
                    entity.trailSnapshots.add(0, Pair.of(new Vec3(currentX, currentY, currentZ), new ModelSnapshot(0, Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot), currentTick, snapshot)));
                    entity.lastTrailTick = currentTick;
                }
                entity.trailSnapshots.removeIf(p -> currentTick - p.getSecond().timestamp() > SNAPSHOT_LIFESPAN);
                while (entity.trailSnapshots.size() > 32) {
                    entity.trailSnapshots.remove(entity.trailSnapshots.size() - 1);
                }
            }
            for (int i = 0; i < entity.trailSnapshots.size(); i++) {
                poseStack.pushPose();
                Vec3 trailPos = entity.trailSnapshots.get(i).getFirst();
                ModelSnapshot snapshot = entity.trailSnapshots.get(i).getSecond();
                ModelUtil.loadPoseFromSnapshot(snapshot.poses(), this.shadowModel::getAnyDescendantWithName);
                poseStack.translate(trailPos.x - currentX, trailPos.y - currentY, trailPos.z - currentZ);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - snapshot.yRot()));
                poseStack.scale(-1.0F, -1.0F, 1.0F);
                this.scale(entity, poseStack, partialTicks);
                poseStack.translate(0.0F, -1.5F, 0.0F);
                VertexConsumer vertexConsumer = bufferSource.getBuffer(AFTERIMAGE_RENDER_TYPE);
                float modelAlpha = (1 - Mth.clamp(currentTick - snapshot.timestamp(), 0, SNAPSHOT_LIFESPAN) / SNAPSHOT_LIFESPAN) * 0.35F;
                if (modelAlpha > 0) {
                    this.shadowModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, modelAlpha);
                }
                poseStack.popPose();
            }
        }
    }

    private static void vertex(VertexConsumer p_254095_, Matrix4f p_254477_, Matrix3f p_253948_, int p_253829_, float p_253995_, int p_254031_, int p_253641_, int p_254243_) {
        p_254095_.vertex(p_254477_, p_253995_ - 0.5F, (float)p_254031_ - 0.5F, 0.0F)
                .color(255, 255, 255, 255)
                .uv((float)p_253641_, (float)p_254243_)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(p_253829_)
                .normal(p_253948_, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return TEXTURE;
    }
}
