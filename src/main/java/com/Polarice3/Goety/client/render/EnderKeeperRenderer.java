package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.EnderKeeperModel;
import com.Polarice3.Goety.common.entities.boss.EnderKeeper;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModelPartPose;
import com.Polarice3.Goety.utils.ModelSnapshot;
import com.Polarice3.Goety.utils.ModelUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Map;

public class EnderKeeperRenderer<T extends EnderKeeper> extends MobRenderer<T, EnderKeeperModel<T>> {
    protected static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/enderling/keeper/keeper.png");
    protected static final ResourceLocation DEATH = Goety.location("textures/entity/enderling/keeper/keeper_death.png");

    private static final RenderType TRAIL_RENDER_TYPE = ModRenderType.entityTranslucentNoDepth(TEXTURE_LOCATION);

    private static final float SNAPSHOT_INTERVAL = 1.5F;
    private static final float SNAPSHOT_LIFESPAN = 4.5F;
    private final EnderKeeperModel<T> shadowModel;

    public EnderKeeperRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_, new EnderKeeperModel<>(p_i47208_1_.bakeLayer(ModModelLayer.ENDER_KEEPER)), 0.5F);
        this.addLayer(new GlowLayer<>(this));
        // we need another model with shrunken parts to prevent z-fighting
        this.shadowModel = new EnderKeeperModel<>(p_i47208_1_.bakeLayer(ModModelLayer.ENDER_KEEPER_SHADOW));
    }

    @Override
    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.isIntro() && pEntity.introTick < 5) {
            return;
        }
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        if (pEntity.deathTime > MathHelper.secondsToTicks(2.5F)){
            pMatrixStack.pushPose();
            boolean flag = pEntity.hurtTime > 0;
            float f = Mth.rotLerp(pPartialTicks, pEntity.yBodyRotO, pEntity.yBodyRot);
            float f1 = Mth.rotLerp(pPartialTicks, pEntity.yHeadRotO, pEntity.yHeadRot);
            float f2 = f1 - f;
            float f6 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
            float f71 = this.getBob(pEntity, pPartialTicks);
            this.setupRotations(pEntity, pMatrixStack, f71, f, pPartialTicks);
            pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
            this.scale(pEntity, pMatrixStack, pPartialTicks);
            pMatrixStack.translate(0.0D, -1.501F, 0.0D);
            this.model.prepareMobModel(pEntity, 0.0F, 0.0F, pPartialTicks);
            this.model.setupAnim(pEntity, 0.0F, 0.0F, f71, f2, f6);
            float f8 = MathHelper.secondsToTicks(5);
            float f9 = (pEntity.deathTime - MathHelper.secondsToTicks(2.5F)) / f8;
            float f10 = 1.0F - ((pEntity.deathTime - MathHelper.secondsToTicks(2.5F)) / f8);
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.dragonExplosionAlpha(DEATH));
            this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, f9);
            VertexConsumer ivertexbuilder1 = pBuffer.getBuffer(RenderType.entityDecal(this.getTextureLocation(pEntity)));
            this.model.renderToBuffer(pMatrixStack, ivertexbuilder1, pPackedLight, OverlayTexture.pack(0.0F, flag), f10, f10, f10, 1.0F);
            pMatrixStack.popPose();
        }
        if (pEntity.isAlive()) {
            double currentX = Mth.lerp(pPartialTicks, pEntity.xo, pEntity.getX());
            double currentY = Mth.lerp(pPartialTicks, pEntity.yo, pEntity.getY());
            double currentZ = Mth.lerp(pPartialTicks, pEntity.zo, pEntity.getZ());
            float currentTick = getBob(pEntity, pPartialTicks);
            if (pEntity.trailSnapshots.isEmpty() || currentTick - pEntity.lastTrailTick > SNAPSHOT_INTERVAL) {
                if (pEntity.shouldAddTrailSnapshot()) {
                    Map<String, ModelPartPose> snapshot = ModelUtil.saveModelSnapshot(this.getModel().allPartNames, this.getModel()::getAnyDescendantWithName);
                    pEntity.trailSnapshots.add(0, Pair.of(new Vec3(currentX, currentY, currentZ), new ModelSnapshot(0, Mth.rotLerp(pPartialTicks, pEntity.yBodyRotO, pEntity.yBodyRot), currentTick, snapshot)));
                    pEntity.lastTrailTick = currentTick;
                }
                pEntity.trailSnapshots.removeIf(p -> currentTick - p.getSecond().timestamp() > SNAPSHOT_LIFESPAN);
                while (pEntity.trailSnapshots.size() > 32) {
                    pEntity.trailSnapshots.remove(pEntity.trailSnapshots.size() - 1);
                }
            }
            for (int i = 0; i < pEntity.trailSnapshots.size(); i++) {
                pMatrixStack.pushPose();
                Vec3 trailPos = pEntity.trailSnapshots.get(i).getFirst();
                ModelSnapshot snapshot = pEntity.trailSnapshots.get(i).getSecond();
                ModelUtil.loadPoseFromSnapshot(snapshot.poses(), this.shadowModel::getAnyDescendantWithName);
                pMatrixStack.translate(trailPos.x - currentX, trailPos.y - currentY, trailPos.z - currentZ);
                pMatrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - snapshot.yRot()));
                pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
                this.scale(pEntity, pMatrixStack, pPartialTicks);
                pMatrixStack.translate(0.0F, -1.5F, 0.0F);
                VertexConsumer vertexConsumer = pBuffer.getBuffer(TRAIL_RENDER_TYPE);
                float modelAlpha = (1 - Mth.clamp(currentTick - snapshot.timestamp(), 0, SNAPSHOT_LIFESPAN) / SNAPSHOT_LIFESPAN) * 0.35F;
                if (modelAlpha > 0) {
                    this.shadowModel.renderToBuffer(pMatrixStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, modelAlpha);
                }
                pMatrixStack.popPose();
            }
        }
    }

    @Nullable
    protected RenderType getRenderType(T p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
        if (p_230496_1_.deathTime > MathHelper.secondsToTicks(2.5F)){
            return RenderType.dragonExplosionAlpha(DEATH);
        } else {
            return super.getRenderType(p_230496_1_, p_230496_2_, p_230496_3_, p_230496_4_);
        }
    }

    public ResourceLocation getTextureLocation(T pEntity) {
        return TEXTURE_LOCATION;
    }

    public static class GlowLayer<T extends EnderKeeper, M extends EnderKeeperModel<T>> extends EyesLayer<T, M> {
        private static final RenderType RENDER_TYPE = RenderType.eyes(Goety.location("textures/entity/enderling/keeper/keeper_glow.png"));

        public GlowLayer(RenderLayerParent<T, M> p_i50919_1_) {
            super(p_i50919_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (!p_116986_.isInvisible() && !p_116986_.isDeadOrDying()) {
                VertexConsumer vertexconsumer = p_116984_.getBuffer(this.renderType());
                this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }
}
