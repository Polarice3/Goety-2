package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.world.phys.Vec3;

public class SoulEmitterParticle extends NoRenderParticle {
    private final SoulSummonVFX.Emitter emitter;
    private final Vec3 origin;
    private final float yaw;
    private float spawnRemainder;
    private final boolean alternate;

    protected SoulEmitterParticle(ClientLevel level, double x, double y, double z, MCDEffectOption option) {
        super(level, x, y, z);
        SoulSummonVFX.Emitter[] values = SoulSummonVFX.Emitter.values();
        this.emitter = values[Math.floorMod(option.life, values.length)];
        this.alternate = option.alternate;
        this.origin = new Vec3(x, y, z);
        this.yaw = option.a;
        this.lifetime = this.emitter.lifetime;
        this.hasPhysics = false;
        this.gravity = 0.0F;
    }

    @Override
    public void tick() {
        if (this.age >= this.lifetime) {
            this.remove();
            return;
        }
        this.spawnRemainder = SoulSummonVFX.emitterTick(this.level, this.origin, this.yaw, this.emitter, this.age, this.spawnRemainder, this.alternate);
        ++this.age;
    }

    public static class Provider implements ParticleProvider<MCDEffectOption> {
        @Override
        public Particle createParticle(MCDEffectOption option, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new SoulEmitterParticle(level, x, y, z, option);
        }
    }
}