package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.client.particles.ColoredSpellSquareParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ChargeAura {
    public static final int LAYER_LIFE = 10;
    public static final int LAYER_INTERVAL = 2;
    public static final int WAVE_LIFE = 6;
    public static final int WAVE_INTERVAL = 5;
    public static final float WAVE_SPEED = 1.0F;

    public final Settings settings;
    public int startTick = -1;
    public int endTick = -1;
    public final List<Wave> waves = new ArrayList<>();
    private Vec3 lastCenter;
    private float fireDistance;

    public ChargeAura(Settings settings) {
        this.settings = settings;
    }

    public record Settings(float radius, float depth, float forward, float up, float tintRMin, float tintRMax, float tintGMin, float tintGMax, float tintBMin, float tintBMax, float brightness, int particleColorA, int particleColorB, ParticleOptions flame) {
        public static final Settings SCULK = new Settings(4.68F, 6.24F, 0.5F, 0.5F,
                0.0F, 0.0F, 0.6F, 0.9F, 1.0F, 1.0F, 0.3F,
                0x0080ff, 0x00ffff, ModParticleTypes.BIG_SOUL_FIRE.get());
        public static final Settings NECRO = new Settings(4.68F, 6.24F, 0.5F, 0.5F,
                0.0F, 0.13F, 0.5F, 0.6F, 0.0F, 0.0275F, 0.3F,
                0x88ff00, 0x0ab405, ModParticleTypes.NECRO_FIRE.get());
        public static final Settings END = new Settings(2.14F, 3.84F, 0.5F, 0.5F,
                0.58F, 0.72F, 0.22F, 0.29F, 0.67F, 0.82F, 0.3F,
                0xf4cdf6, 0xae00bc, ModParticleTypes.END_FIRE.get());

        public Settings scaled(float scale) {
            return new Settings(this.radius * scale, this.depth * scale, this.forward * scale, this.up,
                    this.tintRMin, this.tintRMax, this.tintGMin, this.tintGMax, this.tintBMin, this.tintBMax, this.brightness,
                    this.particleColorA, this.particleColorB, this.flame);
        }
    }

    public record Wave(Vec3 origin, float yaw, int spawnTick, float spin, float seed) {
    }

    public boolean isVisible() {
        return this.startTick >= 0;
    }

    public boolean isActive() {
        return this.startTick >= 0 && this.endTick < 0;
    }

    public Vec3 localCenter(LivingEntity entity, float yaw) {
        float rad = yaw * Mth.DEG_TO_RAD;
        return new Vec3(-Mth.sin(rad) * this.settings.forward, entity.getBbHeight() * this.settings.up, Mth.cos(rad) * this.settings.forward);
    }

    public void tick(LivingEntity entity, boolean active) {
        int now = entity.tickCount;
        if (active && !this.isActive()) {
            this.startTick = now;
            this.endTick = -1;
            this.lastCenter = null;
        } else if (!active && this.isActive()) {
            this.endTick = now;
        }

        if (this.isActive()) {
            Vec3 center = entity.position().add(this.localCenter(entity, entity.yBodyRot));
            int age = now - this.startTick;
            RandomSource random = entity.getRandom();
            if (age % WAVE_INTERVAL == 0) {
                this.waves.add(new Wave(center, entity.yBodyRot, now, (random.nextFloat() * 2.0F - 1.0F) * 72.0F, random.nextFloat()));
            }
            this.spawnParticles(entity, center, age == 0, random);
            this.lastCenter = center;
        }

        this.waves.removeIf(wave -> now - wave.spawnTick() >= WAVE_LIFE);
        if (this.endTick >= 0 && now - this.endTick > LAYER_LIFE && this.waves.isEmpty()) {
            this.startTick = -1;
            this.endTick = -1;
        }
    }

    private void spawnParticles(LivingEntity entity, Vec3 center, boolean burst, RandomSource random) {
        float rad = entity.yBodyRot * Mth.DEG_TO_RAD;
        Vec3 forward = new Vec3(-Mth.sin(rad), 0.0D, Mth.cos(rad));

        int streaks = 4 + (burst ? 10 : 0);
        for (int i = 0; i < streaks; ++i) {
            Vec3 pos = center.add(randomInBall(random, 5.0D));
            double speed = 0.5D + random.nextDouble() * 1.25D;
            float t = random.nextFloat();
            int a = this.settings.particleColorA, b = this.settings.particleColorB;
            float r = Mth.lerp(t, a >> 16 & 255, b >> 16 & 255) / 255.0F;
            float g = Mth.lerp(t, a >> 8 & 255, b >> 8 & 255) / 255.0F;
            float bl = Mth.lerp(t, a & 255, b & 255) / 255.0F;
            float size = 0.04F + random.nextFloat() * 0.12F;
            int life = 2 + random.nextInt(3);
            entity.level.addParticle(new ColoredSpellSquareParticleOption(size, r, g, bl, life), pos.x, pos.y, pos.z, -forward.x * speed, -forward.y * speed, -forward.z * speed);
        }

        if (this.lastCenter != null) {
            this.fireDistance += (float) center.distanceTo(this.lastCenter) * 2.0F;
        }
        int flames = 3 + (int) this.fireDistance;
        this.fireDistance -= (int) this.fireDistance;
        for (int i = 0; i < flames; ++i) {
            Vec3 pos = center.add(randomInBall(random, 5.0D));
            double speed = 0.15D + random.nextDouble() * 0.35D;
            Vec3 vel = forward.scale(-speed).add((random.nextDouble() - 0.5D) * 0.1D, random.nextDouble() * 0.05D, (random.nextDouble() - 0.5D) * 0.1D);
            entity.level.addParticle(this.settings.flame, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
        }
    }

    private static Vec3 randomInBall(RandomSource random, double radius) {
        double r = radius * Math.cbrt(random.nextDouble());
        double cos = random.nextDouble() * 2.0D - 1.0D;
        double sin = Math.sqrt(1.0D - cos * cos);
        double phi = random.nextDouble() * Math.PI * 2.0D;
        return new Vec3(sin * Math.cos(phi) * r, cos * r, sin * Math.sin(phi) * r);
    }
}