package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface EndLanternEmitter {
    List<Emitter> getEmitters(BlockState state);

    enum Kind {
        GLOW_ORB,
        FIRE,
        FIRE_INVERTED
    }

    record Emitter(Vec3 offset, Kind kind) {
    }

    int MAIN_INTERVAL = 20;
    int GLOW_INTERVAL = 39;
    int ORB_INTERVAL = 18;

    int SPARK_INTERVAL = 10;
    double SPARK_SPAWN_HALF_SIZE = 0.1D;
    double SPARK_MIN_SPEED = 1.5D / 20.0D;
    double SPARK_MAX_SPEED = 2.0D / 20.0D;

    static void tick(Level level, Emitter emitter, double x, double y, double z, int age, RandomSource random) {
        switch (emitter.kind()) {
            case GLOW_ORB -> tickGlowOrb(level, x, y, z, age, random);
            case FIRE -> tickFire(level, x, y, z, age, random, false);
            case FIRE_INVERTED -> tickFire(level, x, y, z, age, random, true);
        }
    }

    private static void tickGlowOrb(Level level, double x, double y, double z, int age, RandomSource random) {
        if (age % ORB_INTERVAL == 0) {
            level.addParticle(ModParticleTypes.END_GLOW_ORB.get(), x, y, z, 0.0D, 0.0D, 0.0D);
        }
        if (age % GLOW_INTERVAL == 0) {
            level.addParticle(ModParticleTypes.END_GLOW_HALO.get(), x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    private static void tickFire(Level level, double x, double y, double z, int age, RandomSource random, boolean inverted) {
        if (age % MAIN_INTERVAL == 0) {
            level.addParticle(inverted ? ModParticleTypes.END_LANTERN_FIRE_INVERTED.get() : ModParticleTypes.END_LANTERN_FIRE.get(), x, y, z, 0.0D, 0.0D, 0.0D);
        }
        if (age % SPARK_INTERVAL == 0) {
            double speed = SPARK_MIN_SPEED + random.nextDouble() * (SPARK_MAX_SPEED - SPARK_MIN_SPEED);
            level.addParticle(ModParticleTypes.END_LANTERN_FIRE_SPARK.get(),
                    x + (random.nextDouble() * 2.0D - 1.0D) * SPARK_SPAWN_HALF_SIZE,
                    y + (random.nextDouble() * 2.0D - 1.0D) * SPARK_SPAWN_HALF_SIZE,
                    z + (random.nextDouble() * 2.0D - 1.0D) * SPARK_SPAWN_HALF_SIZE,
                    0.0D, inverted ? -speed : speed, 0.0D);
        }
    }
}
