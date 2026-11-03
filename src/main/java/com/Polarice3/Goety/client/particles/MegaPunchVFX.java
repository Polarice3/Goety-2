package com.Polarice3.Goety.client.particles;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class MegaPunchVFX {
    public static final int START = 11;
    public static final int DURATION = 44;
    private static final int ABSORB_TICKS = 16;
    private static final int SOULFIRE_TICKS = 40;
    private static final double CM = 0.009D;

    private static final float SOULFIRE_RATE = 4.0F;
    private static final double SOULFIRE_RADIUS = 80.0D;

    private static final float ABSORB_RATE = 50.0F / 20.0F;
    private static final double ABSORB_RADIUS_MIN = 250.0D, ABSORB_RADIUS_RANGE = 250.0D;

    private static final float FLOATER_RATE = 1.0F;
    private static final double FLOATER_PER_UNIT = 40.0D;
    private static final double FLOATER_SPEED_MIN = 100.0D, FLOATER_SPEED_RANGE = 150.0D;
    private static final float FLOATER_SIZE_MIN = 0.05F, FLOATER_SIZE_RANGE = 0.04F;

    private static final float[] SOUL_EMBER = {0.17F, 0.45F, 1.0F};
    private static final float[] NECRO_EMBER = {0.12F, 1.0F, 0.04F};
    private static final float[] SOUL_STREAK_COLOR = {0.6F, 0.9F, 1.0F};

    private MegaPunchVFX() {
    }

    public static void tick(Level level, Vec3 hand, Vec3 lastHand, int animTick, boolean alternate) {
        int age = animTick - START;
        if (age < 0 || age >= DURATION) {
            return;
        }
        RandomSource random = level.getRandom();
        double moved = hand.distanceTo(lastHand);

        float soul = age < SOULFIRE_TICKS ? Mth.sin(Mth.PI * age / SOULFIRE_TICKS) : 0.0F;
        int fires = count(SOULFIRE_RATE * soul, random);
        for (int i = 0; i < fires; i++) {
            soulFire(level, hand, random, alternate);
        }

        if (age < ABSORB_TICKS) {
            int absorbs = count(ABSORB_RATE, random);
            for (int i = 0; i < absorbs; i++) {
                absorb(level, hand, random, alternate);
            }
        }

        int floaters = count(FLOATER_RATE + (float) (moved / (FLOATER_PER_UNIT * CM)), random);
        for (int i = 0; i < floaters; i++) {
            floater(level, lastHand.lerp(hand, random.nextDouble()), random, alternate);
        }
    }

    public static void soulFire(Level level, Vec3 hand, RandomSource random, boolean alternate) {
        Vec3 pos = hand.add(inSphere(random, SOULFIRE_RADIUS * CM));
        ParticleOptions fire = alternate ? ModParticleTypes.NECRO_FIRE.get() : ModParticleTypes.BIG_SOUL_FIRE.get();
        level.addParticle(fire, pos.x, pos.y, pos.z, 0.0D, 0.03D + random.nextDouble() * 0.05D, 0.0D);
    }

    public static void absorb(Level level, Vec3 hand, RandomSource random, boolean alternate) {
        float[] colour = alternate ? NecroVFXPalette.LIME : SOUL_STREAK_COLOR;
        Vec3 dir = inSphere(random, 1.0D);
        if (dir.lengthSqr() < 1.0E-4D) {
            return;
        }
        Vec3 pos = hand.add(dir.normalize().scale((ABSORB_RADIUS_MIN + random.nextDouble() * ABSORB_RADIUS_RANGE) * CM));
        level.addParticle(new GatherTrailParticleOption(colour[0], colour[1], colour[2], hand), pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
    }

    public static void floater(Level level, Vec3 pos, RandomSource random, boolean alternate) {
        float[] colour = alternate ? NECRO_EMBER : SOUL_EMBER;
        Vec3 vel = inSphere(random, 1.0D).normalize().scale((FLOATER_SPEED_MIN + random.nextDouble() * FLOATER_SPEED_RANGE) * CM * 0.05D);
        level.addParticle(new ColoredSpellSquareParticleOption(FLOATER_SIZE_MIN + (random.nextFloat() * FLOATER_SIZE_RANGE), colour[0], colour[1], colour[2], 10 + random.nextInt(11), true), pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
    }

    private static int count(float rate, RandomSource random) {
        int n = Mth.floor(rate);
        return random.nextFloat() < rate - n ? n + 1 : n;
    }

    private static Vec3 inSphere(RandomSource random, double radius) {
        double r = Math.cbrt(random.nextDouble()) * radius;
        double cos = random.nextDouble() * 2.0D - 1.0D;
        double sin = Math.sqrt(1.0D - cos * cos);
        double phi = random.nextDouble() * Math.PI * 2.0D;
        return new Vec3(Math.cos(phi) * sin * r, cos * r, Math.sin(phi) * sin * r);
    }
}
