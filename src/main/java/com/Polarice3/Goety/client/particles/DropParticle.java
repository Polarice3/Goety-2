package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DropParticle extends TextureSheetParticle {
    protected DropParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.xd *= (double)0.3F;
        this.yd = Math.random() * (double)0.2F + (double)0.1F;
        this.zd *= (double)0.3F;
        this.setSize(0.01F, 0.01F);
        this.gravity = 0.06F;
        this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        } else {
            this.yd -= (double)this.gravity;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double)0.98F;
            this.yd *= (double)0.98F;
            this.zd *= (double)0.98F;
            if (this.onGround) {
                if (Math.random() < 0.5D) {
                    this.remove();
                }

                this.xd *= (double)0.7F;
                this.zd *= (double)0.7F;
            }

            BlockPos blockpos = BlockPos.containing(this.x, this.y, this.z);
            double d0 = Math.max(this.level.getBlockState(blockpos).getCollisionShape(this.level, blockpos).max(Direction.Axis.Y, this.x - (double)blockpos.getX(), this.z - (double)blockpos.getZ()), (double)this.level.getFluidState(blockpos).getHeight(this.level, blockpos));
            if (d0 > 0.0D && this.y < (double)blockpos.getY() + d0) {
                this.remove();
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_108492_) {
            this.sprite = p_108492_;
        }

        public Particle createParticle(SimpleParticleType p_108503_, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            DropParticle waterdropparticle = new DropParticle(pLevel, pX, pY, pZ);
            waterdropparticle.pickSprite(this.sprite);
            waterdropparticle.setColor((float) pXSpeed, (float) pYSpeed, (float) pZSpeed);
            return waterdropparticle;
        }
    }
}
