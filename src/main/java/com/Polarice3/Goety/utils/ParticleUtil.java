package com.Polarice3.Goety.utils;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ParticleUtil {
    @Nullable
    public static Particle addParticleInternal(ParticleOptions p_109796_, boolean p_109797_, double p_109798_, double p_109799_, double p_109800_, double p_109801_, double p_109802_, double p_109803_) {
        return addParticleInternal(p_109796_, p_109797_, false, p_109798_, p_109799_, p_109800_, p_109801_, p_109802_, p_109803_);
    }

    @Nullable
    public static Particle addParticleInternal(ParticleOptions p_109805_, boolean p_109806_, boolean p_109807_, double p_109808_, double p_109809_, double p_109810_, double p_109811_, double p_109812_, double p_109813_) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (camera.isInitialized()) {
            ParticleStatus particlestatus = calculateParticleLevel(p_109807_);
            if (p_109806_) {
                return Minecraft.getInstance().particleEngine.createParticle(p_109805_, p_109808_, p_109809_, p_109810_, p_109811_, p_109812_, p_109813_);
            } else if (camera.getPosition().distanceToSqr(p_109808_, p_109809_, p_109810_) > 1024.0D) {
                return null;
            } else {
                return particlestatus == ParticleStatus.MINIMAL ? null : Minecraft.getInstance().particleEngine.createParticle(p_109805_, p_109808_, p_109809_, p_109810_, p_109811_, p_109812_, p_109813_);
            }
        } else {
            return null;
        }
    }

    public static ParticleStatus calculateParticleLevel(boolean p_109768_) {
        ParticleStatus particlestatus = Minecraft.getInstance().options.particles().get();
        if (Minecraft.getInstance().level != null) {
            if (p_109768_ && particlestatus == ParticleStatus.MINIMAL && Minecraft.getInstance().level.random.nextInt(10) == 0) {
                particlestatus = ParticleStatus.DECREASED;
            }

            if (particlestatus == ParticleStatus.DECREASED && Minecraft.getInstance().level.random.nextInt(3) == 0) {
                particlestatus = ParticleStatus.MINIMAL;
            }
        }

        return particlestatus;
    }

    public static void circularParticles(Level level, ParticleOptions particleOptions, Entity entity, float radius){
        circularParticles(level, particleOptions, entity.getX(), entity.getY(), entity.getZ(), radius);
    }

    public static void circularParticles(Level level, ParticleOptions particleOptions, Entity entity, double xSpeed, double ySpeed, double zSpeed, float radius){
        circularParticles(level, particleOptions, entity.getX(), entity.getY(), entity.getZ(), xSpeed, ySpeed, zSpeed, radius);
    }

    public static void circularParticles(Level level, ParticleOptions particleOptions, double x, double y, double z, float radius){
        circularParticles(level, particleOptions, x, y, z, 0, 0, 0, radius);
    }

    public static void circularParticles(Level level, ParticleOptions particleOptions, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float radius){
        float f5 = (float) Math.PI * radius * radius;
        for (int k1 = 0; (float) k1 < f5; ++k1) {
            float f6 = level.getRandom().nextFloat() * ((float) Math.PI * 2F);
            float f7 = Mth.sqrt(level.getRandom().nextFloat()) * radius;
            float f8 = Mth.cos(f6) * f7;
            float f9 = Mth.sin(f6) * f7;
            level.addParticle(particleOptions, x + (double) f8, y, z + (double) f9, xSpeed, ySpeed, zSpeed);
        }
    }
}
