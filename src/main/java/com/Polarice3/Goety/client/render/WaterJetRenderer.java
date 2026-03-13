package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.magic.spells.abyss.WaterJetSpell;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.Polarice3.Goety.utils.ModelUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;

import java.util.Optional;

public class WaterJetRenderer {
    private static final ResourceLocation JET_INNER = Goety.location("textures/entity/water_jet/jet_inner.png");
    private static final ResourceLocation JET_OUTER = Goety.location("textures/entity/water_jet/jet_outer.png");

    private static final float INNER_RADIUS = 0.04F;
    private static final float OUTER_RADIUS = 0.05F;

    public static void renderWaterJet(RenderLevelStageEvent event, Player player, float ticks) {
        int range = 8 + WandUtil.getRangeLevel(player);

        Vec3 playerPos = player.getEyePosition(ticks);
        if (Minecraft.getInstance().level != null) {
            boolean mainHand = WandUtil.getSpellOnHand(player, InteractionHand.MAIN_HAND) instanceof WaterJetSpell;
            ItemStack wand = mainHand ? player.getMainHandItem() : player.getOffhandItem();
            float staffHeight = wand.getItem() instanceof IWand w ? w.getWandVisualHeight(Minecraft.getInstance().level, player, wand) : 0.8F;
            if (player == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
                // first-person staff end position
                int arm = (player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1) * (mainHand ? 1 : -1);
                double fovFactor = 960.0 / (double) Minecraft.getInstance().options.fov().get();
                float horizontalFactor = 0.125F * staffHeight + 0.35F, verticalFactor = 1.5F * staffHeight - 0.45F;
                Vec3 vec3 = Minecraft.getInstance().gameRenderer.getMainCamera().getNearPlane().getPointOnPlane(horizontalFactor * arm, verticalFactor).scale(fovFactor);
                playerPos = player.getEyePosition(ticks).add(vec3);
            } else {
                // third person staff end position
                Optional<Vec3> staffEndPos = ModelUtil.getThirdPersonPlayerHandPosition(
                        player,
                        Minecraft.getInstance().getEntityRenderDispatcher(),
                        Mth.lerp(ticks, player.yBodyRotO, player.yBodyRot),
                        ticks,
                        mainHand ? player.getMainArm() : player.getMainArm().getOpposite(),
                        new Vec3(0, 0.55, -staffHeight)
                );
                if (staffEndPos.isPresent()) {
                    playerPos = staffEndPos.get();
                }
            }
        }
        HitResult trace = player.pick(range, ticks, false);
        Vec3 towards = trace.getLocation();

        Entity entity = MiscCapHelper.getClientTarget(player);
        if (entity != null) {
            towards = new Vec3(
                    Mth.lerp(ticks, entity.xo, entity.getX()),
                    Mth.lerp(ticks, entity.yo, entity.getY()) + entity.getBbHeight() / 2,
                    Mth.lerp(ticks, entity.zo, entity.getZ())
            );
        }
        float speedModifier = getSpeedModifier(player);

        float yaw = MathHelper.positionToYaw(playerPos, towards);
        float pitch = MathHelper.positionToPitch(playerPos, towards);
        float length = (float) playerPos.distanceTo(towards);
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.translate(playerPos.x() - view.x(), playerPos.y() - view.y(), playerPos.z() - view.z());
        poseStack.mulPose(new Quaternionf().rotationX(90 * Mth.DEG_TO_RAD));
        poseStack.mulPose(new Quaternionf().rotationZ((yaw - 90) * Mth.DEG_TO_RAD));
        poseStack.mulPose(new Quaternionf().rotationX(-pitch * Mth.DEG_TO_RAD));
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        renderJetLayer(buffer.getBuffer(ModRenderType.magicBeam(JET_INNER)), poseStack, INNER_RADIUS, length, 16.0F, -(player.tickCount + ticks) * speedModifier);
        renderJetLayer(buffer.getBuffer(ModRenderType.magicBeam(JET_OUTER)), poseStack, OUTER_RADIUS, length, 128.0F, -(player.tickCount + ticks) * speedModifier * 0.2F);

        poseStack.popPose();
        buffer.endBatch();
    }

    private static float getSpeedModifier(Player player) {
        if (WandUtil.enchantedFocus(player)) {
            double efficiency = WandUtil.getPotencyLevel(player) / 5.0F;
            double speedModifier = Mth.lerp(efficiency, 0.4F, 0.6F);
            return (float) speedModifier;
        } else {
            return 0.4F;
        }
    }

    private static void renderJetLayer(VertexConsumer consumer, PoseStack poseStack, float radius, float length, float textureRatio, float textureOffset) {
        PoseStack.Pose pose = poseStack.last();

        float uEnd = length / (radius * textureRatio * 2.0F);

        consumer.vertex(pose.pose(), -radius, 0, -radius).color(-1).uv(textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), -radius, 0, radius).color(-1).uv(textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), -radius, length, radius).color(-1).uv(uEnd + textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), -radius, length, -radius).color(-1).uv(uEnd + textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();

        consumer.vertex(pose.pose(), -radius, 0, -radius).color(-1).uv(textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), radius, 0, -radius).color(-1).uv(textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), radius, length, -radius).color(-1).uv(uEnd + textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), -radius, length, -radius).color(-1).uv(uEnd + textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();

        consumer.vertex(pose.pose(), radius, 0, -radius).color(-1).uv(textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), radius, 0, radius).color(-1).uv(textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), radius, length, radius).color(-1).uv(uEnd + textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), radius, length, -radius).color(-1).uv(uEnd + textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();

        consumer.vertex(pose.pose(), -radius, 0, radius).color(-1).uv(textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), radius, 0, radius).color(-1).uv(textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), radius, length, radius).color(-1).uv(uEnd + textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), -radius, length, radius).color(-1).uv(uEnd + textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
    }
}
