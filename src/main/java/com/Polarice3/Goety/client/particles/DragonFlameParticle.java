package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class DragonFlameParticle extends ReversibleParticle {
    private final SpriteSet spriteSet;
    public boolean isBig = false;
    public boolean isNecro = false;

    protected DragonFlameParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.2f, 0.2f);
        this.quadSize *= 3.0F;
        this.lifetime = 14; //Equation: (Total Frames * Interval) - 2
        this.friction = 0.96F;
        this.gravity = -0.1F;
        this.hasPhysics = true;
        this.xd = this.xd * (double)0.01F + vx;
        this.yd = this.yd * (double)0.01F + vy;
        this.zd = this.zd * (double)0.01F + vz;
        this.setSpriteFromAge(spriteSet);
    }

    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            this.xd += this.random.nextFloat() / 500.0F * (float)(this.random.nextBoolean() ? 1 : -1);
            this.zd += this.random.nextFloat() / 500.0F * (float)(this.random.nextBoolean() ? 1 : -1);
            this.setSprite(this.spriteSet.get((this.age / 2) % 8 + 1, 8));
            ParticleOptions ground = ModParticleTypes.SMALL_DRAGON_FLAME_GROUND.get();
            if (this.isNecro) {
                ground = ModParticleTypes.NECRO_DRAGON_FLAME_GROUND.get();
            }
            if (this.isBig) {
                if (this.random.nextFloat() <= 0.25F) {
                    this.level.addParticle(ModParticleTypes.DRAGON_FLAME_DROP.get(), this.x, this.y, this.z, this.xd, this.yd, this.zd);
                }
                ground = ModParticleTypes.BIG_SOUL_FIRE_GROUND.get();
            }
            if (this.onGround) {
                this.level.addParticle(ground, this.x, this.y + 0.5D, this.z, 0.0D, 0.0D, 0.0D);
                this.remove();
            }
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            DragonFlameParticle flameParticle = new DragonFlameParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            flameParticle.isBig = true;
            return flameParticle;
        }
    }

    public static class ReversibleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public ReversibleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            DragonFlameParticle flameParticle = new DragonFlameParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            flameParticle.isBig = true;
            flameParticle.reversed = worldIn.getRandom().nextBoolean();
            return flameParticle;
        }
    }

    public static class SmallProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public SmallProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            DragonFlameParticle flameparticle = new DragonFlameParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            flameparticle.scale(0.5F);
            return flameparticle;
        }
    }

    public static class SmallReversibleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public SmallReversibleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            DragonFlameParticle flameparticle = new DragonFlameParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            flameparticle.scale(0.5F);
            flameparticle.reversed = worldIn.getRandom().nextBoolean();
            return flameparticle;
        }
    }

    public static class NecroProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public NecroProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            DragonFlameParticle flameparticle = new DragonFlameParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            flameparticle.scale(0.5F);
            flameparticle.isNecro = true;
            flameparticle.reversed = worldIn.getRandom().nextBoolean();
            return flameparticle;
        }
    }
}
