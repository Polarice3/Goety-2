package com.Polarice3.Goety.client.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.CorruptedBeamRenderer;
import com.Polarice3.Goety.client.render.ModRenderType;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, value = Dist.CLIENT)
public class EnchantBeamEvent {
    public static final ResourceLocation BEAM_TEXTURE = Goety.location("textures/particle/enchanted_beam.png");
    private static final int SEGMENTS = 24;
    private static final float TETHER_WIDTH = 0.35F;
    private static final int TETHER_COLOR = 0xff9f34cd;
    private static final float ARC_PERIOD = 50.0F;
    private static final float ARC_AMOUNT = 0.35F;
    private static final float UV_TILING = 1.5F;

    @SubscribeEvent
    public static void renderBeams(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level != null) {
                float partialTick = event.getPartialTick();
                Vec3 origin = event.getCamera().getPosition();
                Frustum frustum = event.getFrustum();
                MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
                CorruptedBeamRenderer.renderQueued(bufferSource);
                boolean drewAny = false;

                for (Entity entity : level.entitiesForRendering()) {
                    if (!(entity instanceof LivingEntity enchanted) || enchanted.isRemoved()) {
                        continue;
                    }
                    Entity enchanter = MiscCapHelper.getEnchanter(enchanted);
                    if (enchanter == null || enchanter.isRemoved()) {
                        continue;
                    }
                    Vec3 target = getTargetPos(enchanted, partialTick);
                    Vec3 source = getSourcePos(enchanter, partialTick);
                    if (!frustum.isVisible(beamBounds(source, target))) {
                        continue;
                    }
                    renderBeam(enchanted, source, target, origin, partialTick, event.getPoseStack(), bufferSource);
                    drewAny = true;
                }

                if (drewAny) {
                    bufferSource.endBatch(ModRenderType.enchantedBeam(BEAM_TEXTURE));
                }
            }
        }
    }

    private static AABB beamBounds(Vec3 source, Vec3 target) {
        double pad = ARC_AMOUNT * source.distanceTo(target) + TETHER_WIDTH;
        return new AABB(source, target).inflate(pad);
    }

    private static Vec3 getTargetPos(LivingEntity enchanted, float partialTick) {
        return lerpPosition(enchanted, partialTick).add(0.0D, enchanted.getBbHeight() * 0.5D, 0.0D);
    }

    private static Vec3 lerpPosition(Entity entity, float partialTick) {
        return new Vec3(
                Mth.lerp(partialTick, entity.xo, entity.getX()),
                Mth.lerp(partialTick, entity.yo, entity.getY()),
                Mth.lerp(partialTick, entity.zo, entity.getZ()));
    }

    private static void renderBeam(LivingEntity enchanted, Vec3 source, Vec3 target, Vec3 origin, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        float time = enchanted.tickCount + partialTick + enchanted.getId() * 7.0F;
        float arcPhase = (time % ARC_PERIOD) / ARC_PERIOD;

        VertexConsumer consumer = bufferSource.getBuffer(ModRenderType.enchantedBeam(BEAM_TEXTURE));
        renderRibbon(origin, source, target, arcPhase, TETHER_WIDTH, TETHER_COLOR, poseStack, consumer);
    }


    public static Vec3 getSourcePos(Entity enchanter, float partialTick) {
        double d0 = Mth.lerp(partialTick, enchanter.xo, enchanter.getX());
        double d1 = Mth.lerp(partialTick, enchanter.yo, enchanter.getY()) + (double)enchanter.getBbHeight() * 0.5D;
        double d2 = Mth.lerp(partialTick, enchanter.zo, enchanter.getZ());
        return new Vec3(d0, d1, d2);
    }

    private static Vec3 hermite(Vec3 p0, Vec3 p1, Vec3 t0, Vec3 t1, float t) {
        float t2 = t * t;
        float t3 = t2 * t;
        float h00 = 2.0F * t3 - 3.0F * t2 + 1.0F;
        float h10 = t3 - 2.0F * t2 + t;
        float h01 = -2.0F * t3 + 3.0F * t2;
        float h11 = t3 - t2;
        return p0.scale(h00).add(t0.scale(h10)).add(p1.scale(h01)).add(t1.scale(h11));
    }

    private static void renderRibbon(Vec3 origin, Vec3 from, Vec3 to, float arcPhase, float width, int argb, PoseStack poseStack, VertexConsumer consumer) {
        double distance = to.distanceTo(from);
        if (distance < 1.0E-4D) {
            return;
        }

        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double arc = Mth.sin(arcPhase * Mth.PI) * ARC_AMOUNT * distance;
        Vec3 tangent0 = new Vec3(0.0D, arc, 0.0D);
        Vec3 tangent1 = new Vec3(0.0D, -0.125D * distance, 0.0D);

        Matrix4f pose = poseStack.last().pose();
        float half = width * 0.5F;
        Vec3 previous = from;

        for (int i = 0; i < SEGMENTS; ++i) {
            float t0 = (float) i / SEGMENTS;
            float t1 = (float) (i + 1) / SEGMENTS;
            Vec3 current = hermite(from, to, tangent0, tangent1, t1);

            Vec3 segment = current.subtract(previous);
            Vec3 toCamera = previous.subtract(camera);
            if (segment.lengthSqr() < 1.0E-8D || toCamera.lengthSqr() < 1.0E-8D) {
                previous = current;
                continue;
            }

            Vec3 side = segment.normalize().cross(toCamera.normalize());
            if (side.lengthSqr() < 1.0E-8D) {
                previous = current;
                continue;
            }
            side = side.normalize().scale(half);

            float v0 = t0 * (float) distance * UV_TILING;
            float v1 = t1 * (float) distance * UV_TILING;

            vertexAt(consumer, pose, previous.add(side).subtract(origin), 0.0F, v0, argb);
            vertexAt(consumer, pose, previous.subtract(side).subtract(origin), 1.0F, v0, argb);
            vertexAt(consumer, pose, current.subtract(side).subtract(origin), 1.0F, v1, argb);
            vertexAt(consumer, pose, current.add(side).subtract(origin), 0.0F, v1, argb);

            previous = current;
        }
    }

    private static void vertexAt(VertexConsumer consumer, Matrix4f pose, Vec3 position, float u, float v, int argb) {
        consumer.vertex(pose, (float) position.x, (float) position.y, (float) position.z)
                .color(ColorUtil.ARGB.red(argb), ColorUtil.ARGB.green(argb), ColorUtil.ARGB.blue(argb), ColorUtil.ARGB.alpha(argb))
                .uv(u, v)
                .endVertex();
    }
}