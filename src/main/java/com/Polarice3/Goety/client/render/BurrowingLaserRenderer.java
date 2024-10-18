package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.spells.geomancy.BurrowingSpell;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;

/**
 * Based on @direwolf20's Mining Laser Rendering codes: <a href="https://github.com/Direwolf20-MC/MiningGadgets/blob/mc/1.20.1/src/main/java/com/direwolf20/mininggadgets/client/renderer/RenderMiningLaser.java">...</a>
 */
public class BurrowingLaserRenderer {
    private final static ResourceLocation laserBeam = Goety.location("textures/entity/burrow/laser.png");
    private final static ResourceLocation laserBeam2 = Goety.location("textures/entity/burrow/laser2.png");
    private final static ResourceLocation laserBeamGlow = Goety.location("textures/entity/burrow/laser_glow.png");

    public static void renderLaser(RenderLevelStageEvent event, Player player, float ticks) {
        int range = 16 + WandUtil.getLevels(ModEnchantments.RANGE.get(), player);

        Vec3 playerPos = player.getEyePosition(ticks);
        HitResult trace = player.pick(range, 0.0F, false);

        float speedModifier = getSpeedModifier(player);

        drawLasers(event, playerPos, trace, 0, 0, 0, 0.02F, player, ticks, speedModifier);
    }

    private static float getSpeedModifier(Player player) {
        if (WandUtil.enchantedFocus(player)) {
            double efficiency = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player) / 5.0F;
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
        matrix.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(ticks, -player.getYRot(), -player.yRotO)));
        matrix.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(ticks, player.getXRot(), player.xRotO)));

        PoseStack.Pose matrixstack$entry = matrix.last();
        Matrix3f matrixNormal = matrixstack$entry.normal();
        Matrix4f positionMatrix = matrixstack$entry.pose();

        ColorUtil colorUtil = new ColorUtil(0xfff2d2);
        //additive laser beam
        builder = buffer.getBuffer(ModRenderType.magicBeam(laserBeam));
        drawBeam(xOffset, yOffset, zOffset, builder, positionMatrix, matrixNormal, additiveThickness, activeHand, distance, 0.5F, 1, ticks, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 0.7F);

        //main laser, colored part
        builder = buffer.getBuffer(ModRenderType.magicBeam(laserBeam2));
        drawBeam(xOffset, yOffset, zOffset, builder, positionMatrix, matrixNormal, thickness, activeHand, distance, v, v + distance * 1.5F, ticks, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);

        //core
        builder = buffer.getBuffer(ModRenderType.magicBeam(laserBeamGlow));
        drawBeam(xOffset, yOffset, zOffset, builder, positionMatrix, matrixNormal, thickness/2, activeHand, distance, v, v + distance * 1.5F, ticks, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
        matrix.popPose();
        buffer.endBatch();
    }

    private static float calculateLaserFlickerModifier(long gameTime) {
        return 0.9F + 0.1F * Mth.sin(gameTime * 0.99F) * Mth.sin(gameTime * 0.3F) * Mth.sin(gameTime * 0.1F);
    }

    private static void drawBeam(double xOffset, double yOffset, double zOffset, VertexConsumer builder, Matrix4f positionMatrix, Matrix3f matrixNormalIn, float thickness, InteractionHand hand, double distance, double v1, double v2, float ticks, float r, float g, float b, float alpha) {
        Vector3f vector3f = new Vector3f(0.0F, 1.0F, 0.0F);
        vector3f.transform(matrixNormalIn);
        LocalPlayer player = Minecraft.getInstance().player;
        if( Minecraft.getInstance().options.mainHand().get() != HumanoidArm.RIGHT )
            hand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        float startXOffset = -0.20F;
        float startYOffset = -0.108F;
        float startZOffset = 0.60F;

        startZOffset += (1 - player.getFieldOfViewModifier());
        if (hand == InteractionHand.OFF_HAND) {
            startYOffset = -0.120F;
            startXOffset = 0.25F;
        }
        float f = (Mth.lerp(ticks, player.xRotO, player.getXRot()) - Mth.lerp(ticks, player.xBobO, player.xBob));
        float f1 = (Mth.lerp(ticks, player.yRotO, player.getYRot()) - Mth.lerp(ticks, player.yBobO, player.yBob));
        startXOffset = startXOffset + (f1 / 750);
        startYOffset = startYOffset + (f / 750);

        Vector4f vec1 = new Vector4f(startXOffset, -thickness + startYOffset, startZOffset, 1.0F);
        vec1.transform(positionMatrix);
        Vector4f vec2 = new Vector4f((float) xOffset, -thickness + (float) yOffset, (float) distance + (float) zOffset, 1.0F);
        vec2.transform(positionMatrix);
        Vector4f vec3 = new Vector4f((float) xOffset, thickness + (float) yOffset, (float) distance + (float) zOffset, 1.0F);
        vec3.transform(positionMatrix);
        Vector4f vec4 = new Vector4f(startXOffset, thickness + startYOffset, startZOffset, 1.0F);
        vec4.transform(positionMatrix);

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
