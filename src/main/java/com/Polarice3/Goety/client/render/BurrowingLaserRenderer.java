package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.spells.geomancy.BurrowingSpell;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModelUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Optional;

/**
 * Based on @direwolf20's Mining Laser Rendering codes: <a href="https://github.com/Direwolf20-MC/MiningGadgets/blob/mc/1.20.1/src/main/java/com/direwolf20/mininggadgets/client/renderer/RenderMiningLaser.java">...</a>
 */
public class BurrowingLaserRenderer {
    private final static ResourceLocation laserBeam = Goety.location("textures/entity/burrow/laser.png");
    private final static ResourceLocation laserBeam2 = Goety.location("textures/entity/burrow/laser2.png");
    private final static ResourceLocation laserBeamGlow = Goety.location("textures/entity/burrow/laser_glow.png");

    public static void renderLaser(RenderLevelStageEvent event, Player player, float ticks) {
        int range = 16 + WandUtil.getRangeLevel(player);

        Vec3 playerPos = player.getEyePosition(ticks);
        if (Minecraft.getInstance().level != null) {
            boolean mainHand = WandUtil.getSpellOnHand(player, InteractionHand.MAIN_HAND) instanceof BurrowingSpell;
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

        float speedModifier = getSpeedModifier(player);

        drawLasers(event, playerPos, trace, 0, 0, 0, 0.02F, player, ticks, speedModifier);
    }

    private static float getSpeedModifier(Player player) {
        if (WandUtil.enchantedFocus(player)) {
            double efficiency = WandUtil.getPotencyLevel(player) / 5.0F;
            double speedModifier = Mth.lerp(efficiency, 0.02F, 0.05F);
            return (float) -speedModifier;
        } else {
            return -0.02F;
        }
    }

    private static void drawLasers(RenderLevelStageEvent event, Vec3 from, HitResult trace, double xOffset, double yOffset, double zOffset, float thickness, Player player, float ticks, float speedModifier) {
        InteractionHand activeHand;
        if (WandUtil.getSpellOnHand(player, InteractionHand.MAIN_HAND) instanceof BurrowingSpell) {
            activeHand = InteractionHand.MAIN_HAND;
        } else {
            activeHand = InteractionHand.OFF_HAND;
        }

        VertexConsumer builder;
        double distance = Math.max(1, from.subtract(trace.getLocation()).length());
        long gameTime = player.level.getGameTime();
        double v = gameTime * speedModifier;
        float additiveThickness = (thickness * 3.5F) * calculateLaserFlickerModifier(gameTime);

        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        PoseStack matrix = event.getPoseStack();

        matrix.pushPose();

        matrix.translate(-view.x(), -view.y(), -view.z());
        matrix.translate(from.x, from.y, from.z);
        float yaw = MathHelper.positionToYaw(from, trace.getLocation());
        float pitch = MathHelper.positionToPitch(from, trace.getLocation());
        matrix.mulPose(Axis.YP.rotationDegrees(90 - yaw));
        matrix.mulPose(Axis.XP.rotationDegrees(-pitch));

        PoseStack.Pose matrixstack$entry = matrix.last();
        Matrix3f matrixNormal = matrixstack$entry.normal();
        Matrix4f positionMatrix = matrixstack$entry.pose();

        ColorUtil colorUtil = new ColorUtil(0xfff2d2);
        if (WandUtil.getLevels(ModEnchantments.BURNING.get(), player) > 0){
            colorUtil = new ColorUtil(0xff9166);
        }
        //additive laser beam
        builder = buffer.getBuffer(ModRenderType.magicBeam(laserBeam));
        drawBeam(xOffset, yOffset, zOffset, builder, positionMatrix, matrixNormal, additiveThickness, activeHand, distance, 0.5F, 1, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 0.7F);

        //main laser, colored part
        builder = buffer.getBuffer(ModRenderType.magicBeam(laserBeam2));
        drawBeam(xOffset, yOffset, zOffset, builder, positionMatrix, matrixNormal, thickness, activeHand, distance, v, v + distance * 1.5F, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);

        //core
        builder = buffer.getBuffer(ModRenderType.magicBeam(laserBeamGlow));
        drawBeam(xOffset, yOffset, zOffset, builder, positionMatrix, matrixNormal, thickness / 2, activeHand, distance, v, v + distance * 1.5F, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
        matrix.popPose();
        buffer.endBatch();
    }

    private static float calculateLaserFlickerModifier(long gameTime) {
        return 0.9F + 0.1F * Mth.sin(gameTime * 0.99F) * Mth.sin(gameTime * 0.3F) * Mth.sin(gameTime * 0.1F);
    }

    private static void drawBeam(double xOffset, double yOffset, double zOffset, VertexConsumer builder, Matrix4f positionMatrix, Matrix3f matrixNormalIn, float thickness, InteractionHand hand, double distance, double v1, double v2, float r, float g, float b, float alpha) {
        Vector3f vector3f = new Vector3f(0.0F, 1.0F, 0.0F);
        vector3f.mul(matrixNormalIn);

        Vector4f vec1 = new Vector4f(0.0F, -thickness, 0.0F, 1.0F);
        vec1.mul(positionMatrix);
        Vector4f vec2 = new Vector4f((float) xOffset, -thickness + (float) yOffset, (float) distance + (float) zOffset, 1.0F);
        vec2.mul(positionMatrix);
        Vector4f vec3 = new Vector4f((float) xOffset, thickness + (float) yOffset, (float) distance + (float) zOffset, 1.0F);
        vec3.mul(positionMatrix);
        Vector4f vec4 = new Vector4f(0.0F, thickness, 0.0F, 1.0F);
        vec4.mul(positionMatrix);

        if (hand == InteractionHand.MAIN_HAND) {
            builder.vertex(vec4.x(), vec4.y(), vec4.z(), r, g, b, alpha, 0, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec3.x(), vec3.y(), vec3.z(), r, g, b, alpha, 0, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec2.x(), vec2.y(), vec2.z(), r, g, b, alpha, 1, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec1.x(), vec1.y(), vec1.z(), r, g, b, alpha, 1, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            //Rendering a 2nd time to allow you to see both sides in multiplayer, shouldn't be necessary with culling disabled but here we are....
            builder.vertex(vec1.x(), vec1.y(), vec1.z(), r, g, b, alpha, 1, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec2.x(), vec2.y(), vec2.z(), r, g, b, alpha, 1, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec3.x(), vec3.y(), vec3.z(), r, g, b, alpha, 0, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec4.x(), vec4.y(), vec4.z(), r, g, b, alpha, 0, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
        } else {
            builder.vertex(vec1.x(), vec1.y(), vec1.z(), r, g, b, alpha, 1, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec2.x(), vec2.y(), vec2.z(), r, g, b, alpha, 1, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec3.x(), vec3.y(), vec3.z(), r, g, b, alpha, 0, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec4.x(), vec4.y(), vec4.z(), r, g, b, alpha, 0, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            //Rendering a 2nd time to allow you to see both sides in multiplayer, shouldn't be necessary with culling disabled but here we are....
            builder.vertex(vec4.x(), vec4.y(), vec4.z(), r, g, b, alpha, 0, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec3.x(), vec3.y(), vec3.z(), r, g, b, alpha, 0, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec2.x(), vec2.y(), vec2.z(), r, g, b, alpha, 1, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec1.x(), vec1.y(), vec1.z(), r, g, b, alpha, 1, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
        }
    }
}
