package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.client.render.ModRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;

import java.util.*;

/***
 * This class is based of BoltRenderer Code from Botania: "<a href="https://github.com/VazkiiMods/Botania/blob/1.19.x/Xplat/src/main/java/vazkii/botania/client/fx/BoltRenderer.java">...</a>">...</a>
 */
public class LightningEffect {

    public static final LightningEffect INSTANCE = new LightningEffect();
    private static final float REFRESH_TIME = 3F;
    private static final double LIFETIME_AFTER_LAST_BOLT = 100;
    private Timestamp refreshTimestamp = Timestamp.ZERO;

    private final Random random = new Random();

    private final List<BoltEmitter> boltEmitters = new LinkedList<>();

    public static void onWorldRenderLast(Camera camera, float partialTicks, PoseStack ps, RenderBuffers buffers) {
        ps.pushPose();
        Vec3 camVec = camera.getPosition();
        ps.translate(-camVec.x, -camVec.y, -camVec.z);
        MultiBufferSource.BufferSource bufferSource = buffers.bufferSource();
        LightningEffect.INSTANCE.render(partialTicks, ps, bufferSource);
        bufferSource.endBatch(ModRenderType.LIGHTNING);
        ps.popPose();
    }

    public void render(float partialTicks, PoseStack matrixStack, MultiBufferSource buffers) {
        if (Minecraft.getInstance().level != null) {
            VertexConsumer buffer = buffers.getBuffer(ModRenderType.LIGHTNING);
            Matrix4f matrix = matrixStack.last().pose();
            Timestamp timestamp = new Timestamp(Minecraft.getInstance().level.getGameTime(), partialTicks);
            boolean refresh = timestamp.isPassed(this.refreshTimestamp, (1 / REFRESH_TIME));
            if (refresh) {
                this.refreshTimestamp = timestamp;
            }

            for (Iterator<BoltEmitter> iter = this.boltEmitters.iterator(); iter.hasNext(); ) {
                BoltEmitter emitter = iter.next();
                emitter.renderTick(timestamp, refresh, matrix, buffer);
                if (emitter.shouldRemove(timestamp)) {
                    iter.remove();
                }
            }
        }
    }

    public void add(Level level, LightningParticleOptions options, float partialTicks) {
        if (!level.isClientSide) {
            return;
        }
        BoltEmitter emitter = new BoltEmitter(options);
        Timestamp timestamp = new Timestamp(level.getGameTime(), partialTicks);
        if ((!emitter.options.getSpawnFunction().isConsecutive() || emitter.bolts.isEmpty()) && timestamp.isPassed(emitter.lastBoltTimestamp, emitter.lastBoltDelay)) {
            emitter.addBolt(new BoltInstance(options, timestamp), timestamp);
        }
        emitter.lastUpdateTimestamp = timestamp;
        this.boltEmitters.add(emitter);
    }

    public class BoltEmitter {

        private final Set<BoltInstance> bolts = new ObjectOpenHashSet<>();
        private final LightningParticleOptions options;
        private Timestamp lastBoltTimestamp = Timestamp.ZERO;
        private Timestamp lastUpdateTimestamp = Timestamp.ZERO;
        private double lastBoltDelay;

        public BoltEmitter(LightningParticleOptions options) {
            this.options = options;
        }

        private void addBolt(BoltInstance instance, Timestamp timestamp) {
            this.bolts.add(instance);
            this.lastBoltDelay = instance.options.getSpawnFunction().getSpawnDelay(random);
            this.lastBoltTimestamp = timestamp;
        }

        public void renderTick(Timestamp timestamp, boolean refresh, Matrix4f matrix, VertexConsumer buffer) {
            if (refresh) {
                this.bolts.removeIf(bolt -> bolt.tick(timestamp));
            }
            if (this.bolts.isEmpty() && this.options != null && this.options.getSpawnFunction().isConsecutive()) {
                addBolt(new BoltInstance(this.options, timestamp), timestamp);
            }
            this.bolts.forEach(bolt -> bolt.render(matrix, buffer, timestamp));
        }

        public boolean shouldRemove(Timestamp timestamp) {
            return this.bolts.isEmpty() && timestamp.isPassed(this.lastUpdateTimestamp, LIFETIME_AFTER_LAST_BOLT);
        }
    }

    private static class BoltInstance {

        private final LightningParticleOptions options;
        private final List<LightningParticleOptions.BoltQuads> renderQuads;
        private final Timestamp createdTimestamp;

        public BoltInstance(LightningParticleOptions options, Timestamp timestamp) {
            this.options = options;
            this.renderQuads = options.generate();
            this.createdTimestamp = timestamp;
        }

        public void render(Matrix4f matrix, VertexConsumer buffer, Timestamp timestamp) {
            float lifeScale = timestamp.subtract(this.createdTimestamp).value() / this.options.getLifespan();
            Pair<Integer, Integer> bounds = options.getFadeFunction().getRenderBounds(this.renderQuads.size(), lifeScale);
            for (int i = bounds.getLeft(); i < bounds.getRight(); i++) {
                this.renderQuads.get(i).getVecs().forEach(v -> buffer.vertex(matrix, (float) v.x, (float) v.y, (float) v.z)
                        .color(this.options.getColor().red(), this.options.getColor().green(), this.options.getColor().blue(), options.getColor().alpha())
                        .endVertex());
            }
        }

        public boolean tick(Timestamp timestamp) {
            return timestamp.isPassed(this.createdTimestamp, this.options.getLifespan());
        }
    }

    private record Timestamp(long ticks, float partial) {

            public static final Timestamp ZERO = new Timestamp(0, 0);

        public Timestamp subtract(Timestamp other) {
                long newTicks = this.ticks - other.ticks;
                float newPartial = this.partial - other.partial;
                if (newPartial < 0) {
                    newPartial += 1;
                    newTicks -= 1;
                }
                return new Timestamp(newTicks, newPartial);
            }

            public float value() {
                return this.ticks + this.partial;
            }

            public boolean isPassed(Timestamp prev, double duration) {
                long ticksPassed = this.ticks - prev.ticks;
                if (ticksPassed > duration) {
                    return true;
                }
                duration -= ticksPassed;
                if (duration >= 1) {
                    return false;
                }
                return (this.partial - prev.partial) >= duration;
            }
        }
}
