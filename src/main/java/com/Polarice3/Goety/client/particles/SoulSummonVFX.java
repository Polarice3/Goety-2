package com.Polarice3.Goety.client.particles;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class SoulSummonVFX {
    public static final int STREAM_TICKS = 28;
    private static final float[][] SOUL_SMOKE = {{0.0F, 0.6F, 1.0F}, {0.1F, 0.8F, 1.0F}, {0.2F, 1.0F, 1.0F}};
    private static final float[] WHITE = {1.0F, 1.0F, 1.0F};

    private SoulSummonVFX() {
    }

    public static void emitter(ServerLevel level, Vec3 pos, Emitter emitter, float yawDeg, boolean alternate) {
        level.sendParticles(new MCDEffectOption(ModParticleTypes.SOUL_EMITTER.get(), 1.0F, emitter.ordinal(), yawDeg, 0.0F, 0.0F, alternate), pos.x, pos.y, pos.z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    public static void emitter(ServerLevel level, Vec3 pos, Emitter emitter, float yawDeg) {
        emitter(level, pos, emitter, yawDeg, false);
    }

    public static void emitter(ServerLevel level, Vec3 pos, Emitter emitter) {
        emitter(level, pos, emitter, 0.0F, false);
    }

    public static float emitterTick(Level level, Vec3 origin, float yawDeg, Emitter emitter, int age, float remainder, boolean alternate) {
        RandomSource random = level.getRandom();
        switch (emitter) {
            case CUBE_SPAWN -> {
                if (age == 0) {
                    int souls = 2 + random.nextInt(2);
                    for (int i = 0; i < souls; i++) {
                        bombSoul(level, origin, random, alternate);
                    }
                    int floating = 12 + random.nextInt(9);
                    for (int i = 0; i < floating; i++) {
                        cubeFloatingSpark(level, origin, random, alternate);
                    }
                    int dust = 12 + random.nextInt(7);
                    for (int i = 0; i < dust; i++) {
                        cubeDust(level, origin, random, alternate);
                    }
                }
                if (age == 1) {
                    int sparks = 24 + random.nextInt(7);
                    for (int i = 0; i < sparks; i++) {
                        soulStreak(level, origin, random, alternate);
                    }
                    int lava = 12 + random.nextInt(7);
                    for (int i = 0; i < lava; i++) {
                        soulLava(level, origin, random, alternate);
                    }
                }
                if (age < 10) {
                    remainder += 18.0F * 0.05F;
                    while (remainder >= 1.0F) {
                        soulLava(level, origin, random, alternate);
                        remainder -= 1.0F;
                    }
                }
            }
            case SOUL_BOMB -> {
                if (age == 0) {
                    for (int i = 0; i < 6; i++) {
                        bombSoul(level, origin, random, alternate);
                    }
                }
            }
        }
        return remainder;
    }

    public static float emitterTick(Level level, Vec3 origin, float yawDeg, Emitter emitter, int age, float remainder) {
        return emitterTick(level, origin, yawDeg, emitter, age, remainder, false);
    }

    public static void soulOrbit(Level level, Vec3 origin, RandomSource random, boolean alternate) {
        Vec3 offset = inSphere(random, 1.0D);
        double x = origin.x + offset.x, y = origin.y + offset.y, z = origin.z + offset.z;
        level.addParticle(new MCDEffectOption(ModParticleTypes.SOUL_ORBIT.get(), 0.55F, 20 + random.nextInt(21), (float) -offset.x, 1.0F, (float) -offset.z, alternate), x, y, z, 0.0D, 0.25D + random.nextDouble() * 0.25D, 0.0D);
    }

    public static void soulStreak(Level level, Vec3 origin, RandomSource random, boolean alternate) {
        Vec3 vec3 = cone(random, 0.0D, 75.0D).scale(0.9D + random.nextDouble() * 1.8D);
        level.addParticle(new MCDEffectOption(ModParticleTypes.SOUL_STREAK.get(), 0.13F + random.nextFloat() * 0.02F, 4 + random.nextInt(5), 0.5F + random.nextFloat() * 0.5F, 0.0F, 0.0F, alternate), origin.x, origin.y, origin.z, vec3.x, vec3.y, vec3.z);
    }

    public static void soulLava(Level level, Vec3 origin, RandomSource random, boolean alternate) {
        double halfAngle = random.nextDouble() * 45.0D;
        Vec3 vec3 = cone(random, 0.0D, halfAngle).scale(0.9D + random.nextDouble() * 0.9D);
        float k = 0.2F + random.nextFloat() * 0.8F;
        float[] hue = alternate ? NecroVFXPalette.greenHue(k) : new float[]{0.0F, k, 1.0F};
        level.addParticle(new MCDEffectOption(ModParticleTypes.SOUL_LAVA.get(), 0.13F + random.nextFloat() * 0.02F, 20 + random.nextInt(21), hue[0], hue[1], hue[2], alternate), origin.x, origin.y, origin.z, vec3.x, vec3.y, vec3.z);
    }

    public static void cubeFloatingSpark(Level level, Vec3 origin, RandomSource random, boolean alternate) {
        Vec3 vec3 = cone(random, 0.0D, 67.5D).scale(0.9D + random.nextDouble() * 0.9D);
        float blue = 5.0F + random.nextFloat() * 5.0F;
        float green = 1.0F + random.nextFloat() * 4.0F;
        float gravity = -(50.0F + random.nextFloat() * 100.0F) / 40000.0F;
        level.addParticle(new MCDEffectOption(ModParticleTypes.SOUL_SPARK.get(), 0.06F + random.nextFloat() * 0.06F, 20 + random.nextInt(21), green / blue, blue, gravity, alternate), origin.x, origin.y, origin.z, vec3.x, vec3.y, vec3.z);
    }

    public static void cubeDust(Level level, Vec3 origin, RandomSource random, boolean alternate) {
        double inner = (0.7D + random.nextDouble() * 0.3D) * 70.0D;
        Vec3 vec3 = cone(random, inner, 85.0D).scale(1.0D + random.nextDouble() * 0.5D);
        float[] colour = alternate
                ? NecroVFXPalette.smokeColour(random.nextInt(NecroVFXPalette.smokeColours()))
                : SOUL_SMOKE[random.nextInt(SOUL_SMOKE.length)];
        level.addParticle(new MCDEffectOption(ModParticleTypes.SOUL_DUST.get(), 1.0F + random.nextFloat(), 16 + random.nextInt(45), colour[0], colour[1], colour[2], alternate), origin.x, origin.y + 0.1D, origin.z, vec3.x, vec3.y, vec3.z);
    }

    public static void bombSoul(Level level, Vec3 origin, RandomSource random, boolean alternate) {
        double r = Math.sqrt(random.nextDouble());
        double angle = random.nextDouble() * Math.PI * 2.0D;
        double ox = Math.cos(angle) * r, oz = Math.sin(angle) * r;
        double out = 0.25D + random.nextDouble() * 0.25D;
        double h = Math.sqrt(ox * ox + oz * oz);
        double dirX, dirZ;
        if (h > 1.0E-4D) {
            dirX = ox / h;
            dirZ = oz / h;
        } else {
            double a = random.nextDouble() * Math.PI * 2.0D;
            dirX = Math.cos(a);
            dirZ = Math.sin(a);
        }
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new MCDEffectOption(ModParticleTypes.SOUL_BURST.get(), 0.55F, 30 + random.nextInt(21), (float) -ox, 1.0F, (float) -oz, alternate), origin.x + ox, origin.y, origin.z + oz, 0, dirX * out, 0.05D + random.nextDouble() * 0.95D, dirZ * out, 1.0F);
        } else {
            level.addParticle(new MCDEffectOption(ModParticleTypes.SOUL_BURST.get(), 0.55F, 30 + random.nextInt(21), (float) -ox, 1.0F, (float) -oz, alternate), origin.x + ox, origin.y, origin.z + oz, dirX * out, 0.05D + random.nextDouble() * 0.95D, dirZ * out);
        }
    }

    public static void burst(Level level, Vec3 origin, float yawDeg, boolean alternate) {
        RandomSource random = level.getRandom();
        int souls = 10 + random.nextInt(11);
        for (int i = 0; i < souls; i++) {
            soul(level, origin, yawDeg, 1.5D, alternate);
        }
        int sparks = 16 + random.nextInt(15);
        for (int i = 0; i < sparks; i++) {
            spark(level, origin, random, alternate);
        }
        int dust = 40 + random.nextInt(11);
        for (int i = 0; i < dust; i++) {
            dust(level, origin, random, alternate);
        }
    }

    public static void burst(Level level, Vec3 origin, float yawDeg) {
        burst(level, origin, yawDeg, false);
    }

    public static void flash(Level level, Vec3 origin, int count, double maxDeg, double scale, boolean alternate) {
        RandomSource random = level.getRandom();
        float[] tint = alternate ? NecroVFXPalette.LIME_HUE : WHITE;
        for (int i = 0; i < count; i++) {
            Vec3 vec3 = cone(random, 0.0D, maxDeg).scale(scale);
            MCDEffectOption option = new MCDEffectOption(ModParticleTypes.SOUL_FLASH.get(), 0.13F + random.nextFloat() * 0.02F, 4 + random.nextInt(5), tint[0], tint[1], tint[2], alternate);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(option, origin.x, origin.y, origin.z, 0, vec3.x, vec3.y, vec3.z, 1.0F);
            } else {
                level.addParticle(option, origin.x, origin.y, origin.z, vec3.x, vec3.y, vec3.z);
            }
        }
    }

    public static void flash(Level level, Vec3 origin, int count, double maxDeg, double scale) {
        flash(level, origin, count, maxDeg, scale, false);
    }

    public static void flash(Level level, Vec3 origin, boolean alternate) {
        RandomSource random = level.getRandom();
        int count = 40 + random.nextInt(11);
        flash(level, origin, count, 75.0D, 1.5D + random.nextDouble() * 3.0D, alternate);
    }

    public static void flash(Level level, Vec3 origin) {
        flash(level, origin, false);
    }

    public static void soulRandom(Level level, Vec3 origin, int life, double xSpeed, double ySpeed, double zSpeed, boolean alternate) {
        RandomSource random = level.getRandom();
        MCDEffectOption option = new MCDEffectOption(ModParticleTypes.SOUL_LEADER.get(), 0.4F + random.nextFloat() * 0.1F, life, 0.0F, 0.5F + random.nextFloat() * 0.5F, 0.0F, alternate);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(option, origin.x, origin.y, origin.z, 0, xSpeed, ySpeed, zSpeed, 1.0F);
        } else {
            level.addParticle(option, origin.x, origin.y, origin.z, xSpeed, ySpeed, zSpeed);
        }
        spark(level, origin, random, alternate);
    }

    public static void soulRandom(Level level, Vec3 origin, int life, double xSpeed, double ySpeed, double zSpeed) {
        soulRandom(level, origin, life, xSpeed, ySpeed, zSpeed, false);
    }

    public static void soul(Level level, Vec3 origin, float yawDeg, double ySpeed, boolean alternate) {
        RandomSource random = level.getRandom();
        float yaw = yawDeg * Mth.DEG_TO_RAD;
        double fx = -Mth.sin(yaw), fz = Mth.cos(yaw);
        double rx = -Mth.cos(yaw), rz = -Mth.sin(yaw);
        double lf = (random.nextDouble() * 2.0D - 1.0D) * 0.91D;
        double lr = (random.nextDouble() * 2.0D - 1.0D) * 1.06D;
        double ox = fx * lf + rx * lr;
        double oz = fz * lf + rz * lr;
        double x = origin.x + ox;
        double y = origin.y + (random.nextBoolean() ? 0.01D : -0.01D);
        double z = origin.z + oz;

        double h = Math.sqrt(ox * ox + oz * oz);
        double out = random.nextDouble() * 0.5D;
        double vx = h > 1.0E-4D ? ox / h * out : 0.0D;
        double vz = h > 1.0E-4D ? oz / h * out : 0.0D;
        double vy = ySpeed + random.nextDouble();

        level.addParticle(new MCDEffectOption(ModParticleTypes.SOUL_LEADER.get(),
                        0.4F + random.nextFloat() * 0.1F, 20 + random.nextInt(21),
                        (float) (origin.x - x), 0.5F + random.nextFloat() * 0.5F, (float) (origin.z - z), alternate),
                x, y, z, vx, vy, vz);
    }

    public static void soul(Level level, Vec3 origin, float yawDeg, double ySpeed) {
        soul(level, origin, yawDeg, ySpeed, false);
    }

    public static void soul(Level level, Vec3 origin, float yawDeg) {
        soul(level, origin, yawDeg, 1.5D, false);
    }

    public static void dust(Level level, Vec3 origin, RandomSource randomSource, boolean alternate) {
        Vec3 v = cone(randomSource, 49.0D, 70.0D).scale(1.0D + randomSource.nextDouble() * 1.5D);
        float k = 0.3F + randomSource.nextFloat() * 0.7F;
        float[] colour;
        if (alternate) {
            colour = NecroVFXPalette.lerp(k, NecroVFXPalette.DARK, NecroVFXPalette.smokeColour(randomSource.nextInt(NecroVFXPalette.smokeColours())));
        } else {
            colour = new float[]{0.0F, (0.7F + randomSource.nextFloat() * 0.3F) * k, k};
        }
        level.addParticle(new MCDEffectOption(ModParticleTypes.SOUL_DUST.get(),
                        1.0F + randomSource.nextFloat(), 16 + randomSource.nextInt(45), colour[0], colour[1], colour[2], alternate),
                origin.x, origin.y + 0.1D, origin.z, v.x, v.y, v.z);
    }

    public static void dust(Level level, Vec3 origin, RandomSource randomSource) {
        dust(level, origin, randomSource, false);
    }

    public static void spark(Level level, Vec3 origin, RandomSource randomSource, boolean alternate) {
        Vec3 vec3 = cone(randomSource, 0.0D, 67.5D).scale(1.5D + randomSource.nextDouble() * 1.5D).add(0.0D, randomSource.nextDouble() * 0.5D, 0.0D);
        float gravity = -(50.0F + randomSource.nextFloat() * 100.0F) / 40000.0F;
        MCDEffectOption option = new MCDEffectOption(ModParticleTypes.SOUL_SPARK.get(), 0.06F + randomSource.nextFloat() * 0.06F, 20 + randomSource.nextInt(21), 0.6F + randomSource.nextFloat() * 0.4F, 1.0F + randomSource.nextFloat() * 29.0F, gravity, alternate);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(option, origin.x, origin.y, origin.z, 0, vec3.x, vec3.y, vec3.z, 1.0F);
        } else {
            level.addParticle(option, origin.x, origin.y, origin.z, vec3.x, vec3.y, vec3.z);
        }
    }

    public static void spark(Level level, Vec3 origin, RandomSource randomSource) {
        spark(level, origin, randomSource, false);
    }

    // ------------------------------------------------------------------ helpers

    private static Vec3 cone(RandomSource randomSource, double minDeg, double maxDeg) {
        double cosOuter = Math.cos(Math.toRadians(maxDeg));
        double cosInner = Math.cos(Math.toRadians(minDeg));
        double cos = Mth.lerp(randomSource.nextDouble(), cosOuter, cosInner);
        double sin = Math.sqrt(Math.max(0.0D, 1.0D - cos * cos));
        double phi = randomSource.nextDouble() * Math.PI * 2.0D;
        return new Vec3(Math.cos(phi) * sin, cos, Math.sin(phi) * sin);
    }

    private static Vec3 inSphere(RandomSource random, double radius) {
        double r = Math.cbrt(random.nextDouble()) * radius;
        double cos = random.nextDouble() * 2.0D - 1.0D;
        double sin = Math.sqrt(1.0D - cos * cos);
        double phi = random.nextDouble() * Math.PI * 2.0D;
        return new Vec3(Math.cos(phi) * sin * r, cos * r, Math.sin(phi) * sin * r);
    }

    public enum Emitter {
        CUBE_SPAWN(11),
        SOUL_BOMB(1);

        public final int lifetime;

        Emitter(int lifetime) {
            this.lifetime = lifetime;
        }
    }
}