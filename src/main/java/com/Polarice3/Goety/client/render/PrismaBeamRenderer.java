package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.magic.spells.abyss.PrismaBeamSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.Polarice3.Goety.utils.ModelUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Optional;

public class PrismaBeamRenderer {
    private static final ResourceLocation GUARDIAN_BEAM_LOCATION = new ResourceLocation("textures/entity/guardian_beam.png");
    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(GUARDIAN_BEAM_LOCATION);

    public static void renderLaser(RenderLevelStageEvent event, Player player, float ticks) {
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        render(player, ticks, event.getPoseStack(), buffer);
    }

    public static void render(Player player, float partialTick, PoseStack poseStack, MultiBufferSource.BufferSource buffer) {
        Entity entity = MiscCapHelper.getClientTarget(player);
        if (entity instanceof LivingEntity livingentity) {
            int count = player.getUseItemRemainingTicks();
            int castTime = player.getUseItem().getUseDuration() - count;
            float f = getAttackAnimationScale(castTime, partialTick);
            float f1 = castTime + partialTick;
            float f2 = f1 * 0.5F % 1.0F;
            poseStack.pushPose();
            Vec3 targetPos = getPosition(livingentity, livingentity.getBbHeight() * 0.5, partialTick);
            Vec3 playerPos = player.getEyePosition(partialTick);
            if (Minecraft.getInstance().level != null) {
                boolean mainHand = WandUtil.getSpellOnHand(player, InteractionHand.MAIN_HAND) instanceof PrismaBeamSpell;
                ItemStack wand = mainHand ? player.getMainHandItem() : player.getOffhandItem();
                float staffHeight = wand.getItem() instanceof IWand w ? w.getWandVisualHeight(Minecraft.getInstance().level, player, wand) : 0.8F;
                if (player == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
                    // first-person staff end position
                    int arm = (player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1) * (mainHand ? 1 : -1);
                    double fovFactor = 960.0 / (double) Minecraft.getInstance().options.fov().get();
                    float horizontalFactor = 0.125F * staffHeight + 0.35F, verticalFactor = 1.5F * staffHeight - 0.45F;
                    Vec3 vec3 = Minecraft.getInstance().gameRenderer.getMainCamera().getNearPlane().getPointOnPlane(horizontalFactor * arm, verticalFactor).scale(fovFactor);
                    playerPos = player.getEyePosition(partialTick).add(vec3);
                } else {
                    // third person staff end position
                    Optional<Vec3> staffEndPos = ModelUtil.getThirdPersonPlayerHandPosition(
                            player,
                            Minecraft.getInstance().getEntityRenderDispatcher(),
                            Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot),
                            partialTick,
                            mainHand ? player.getMainArm() : player.getMainArm().getOpposite(),
                            new Vec3(0, 0.55, -staffHeight)
                    );
                    if (staffEndPos.isPresent()) {
                        playerPos = staffEndPos.get();
                    }
                }
            }
            Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            poseStack.translate(playerPos.x() - view.x(), playerPos.y() - view.y(), playerPos.z() - view.z());
            Vec3 diff = targetPos.subtract(playerPos);
            float f4 = (float) (diff.length() + 1.0D);
            diff = diff.normalize();
            float f5 = (float) Math.acos(diff.y);
            float f6 = (float) Math.atan2(diff.z, diff.x);
            poseStack.mulPose(Axis.YP.rotationDegrees((((float)Math.PI / 2F) - f6) * (180F / (float)Math.PI)));
            poseStack.mulPose(Axis.XP.rotationDegrees(f5 * (180F / (float)Math.PI)));
            float f7 = f1 * 0.05F * -1.5F;
            float f8 = f * f;
            int j = 64 + (int)(f8 * 191.0F);
            int k = 32 + (int)(f8 * 191.0F);
            int l = 128 - (int)(f8 * 64.0F);
            float f11 = Mth.cos(f7 + 2.3561945F) * 0.282F;
            float f12 = Mth.sin(f7 + 2.3561945F) * 0.282F;
            float f13 = Mth.cos(f7 + ((float)Math.PI / 4F)) * 0.282F;
            float f14 = Mth.sin(f7 + ((float)Math.PI / 4F)) * 0.282F;
            float f15 = Mth.cos(f7 + 3.926991F) * 0.282F;
            float f16 = Mth.sin(f7 + 3.926991F) * 0.282F;
            float f17 = Mth.cos(f7 + 5.4977875F) * 0.282F;
            float f18 = Mth.sin(f7 + 5.4977875F) * 0.282F;
            float f19 = Mth.cos(f7 + (float)Math.PI) * 0.2F;
            float f20 = Mth.sin(f7 + (float)Math.PI) * 0.2F;
            float f21 = Mth.cos(f7 + 0.0F) * 0.2F;
            float f22 = Mth.sin(f7 + 0.0F) * 0.2F;
            float f23 = Mth.cos(f7 + ((float)Math.PI / 2F)) * 0.2F;
            float f24 = Mth.sin(f7 + ((float)Math.PI / 2F)) * 0.2F;
            float f25 = Mth.cos(f7 + ((float)Math.PI * 1.5F)) * 0.2F;
            float f26 = Mth.sin(f7 + ((float)Math.PI * 1.5F)) * 0.2F;
            float f29 = -1.0F + f2;
            float f30 = f4 * 2.5F + f29;
            VertexConsumer vertexconsumer = buffer.getBuffer(BEAM_RENDER_TYPE);
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertex(vertexconsumer, matrix4f, matrix3f, f19, f4, f20, j, k, l, 0.4999F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, 0.0F, f22, j, k, l, 0.0F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, f4, f24, j, k, l, 0.4999F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, 0.0F, f26, j, k, l, 0.0F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0F, f30);
            float f31 = 0.0F;
            if (player.tickCount % 2 == 0) {
                f31 = 0.5F;
            }

            vertex(vertexconsumer, matrix4f, matrix3f, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
            vertex(vertexconsumer, matrix4f, matrix3f, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
            vertex(vertexconsumer, matrix4f, matrix3f, f17, f4, f18, j, k, l, 1.0F, f31);
            vertex(vertexconsumer, matrix4f, matrix3f, f15, f4, f16, j, k, l, 0.5F, f31);
            poseStack.popPose();
            buffer.endBatch();
        }
    }

    public static float getAttackAnimationScale(float time, float partialTick) {
        return (time + partialTick) / (float) SpellConfig.PrismaBeamDuration.get();
    }

    private static Vec3 getPosition(LivingEntity livingEntity, double height, float partialTicks) {
        double d0 = Mth.lerp(partialTicks, livingEntity.xOld, livingEntity.getX());
        double d1 = Mth.lerp(partialTicks, livingEntity.yOld, livingEntity.getY()) + height;
        double d2 = Mth.lerp(partialTicks, livingEntity.zOld, livingEntity.getZ());
        return new Vec3(d0, d1, d2);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, float x, float y, float z, int red, int green, int blue, float u, float v) {
        consumer.vertex(pose, x, y, z).color(red, green, blue, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
    }
}
