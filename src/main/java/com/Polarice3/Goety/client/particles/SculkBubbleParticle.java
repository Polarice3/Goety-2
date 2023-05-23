package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

public class SculkBubbleParticle extends TextureSheetParticle {
    private final PositionSource target;

    SculkBubbleParticle(ClientLevel p_234105_, double p_234106_, double p_234107_, double p_234108_, PositionSource p_234109_, int p_234110_) {
        super(p_234105_, p_234106_, p_234107_, p_234108_, 0.0D, 0.0D, 0.0D);
        this.quadSize = 0.3F;
        this.target = p_234109_;
        this.lifetime = p_234110_;
    }

    public int getLightColor(float p_172469_) {
        return 240;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            Optional<Vec3> optional = this.target.getPosition(this.level);
            if (optional.isEmpty()) {
                this.remove();
            } else {
                int i = this.lifetime - this.age;
                double d0 = 1.0D / (double)i;
                Vec3 vec3 = optional.get();
                this.x = Mth.lerp(d0, this.x, vec3.x());
                this.y = Mth.lerp(d0, this.y, vec3.y());
                this.z = Mth.lerp(d0, this.z, vec3.z());
                this.setPos(this.x, this.y, this.z);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SculkBubbleParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_172490_) {
            this.sprite = p_172490_;
        }

        public Particle createParticle(SculkBubbleParticleOption p_172501_, ClientLevel p_172502_, double p_172503_, double p_172504_, double p_172505_, double p_172506_, double p_172507_, double p_172508_) {
            SculkBubbleParticle vibrationsignalparticle = new SculkBubbleParticle(p_172502_, p_172503_, p_172504_, p_172505_, p_172501_.getDestination(), p_172501_.getArrivalInTicks());
            vibrationsignalparticle.pickSprite(this.sprite);
            vibrationsignalparticle.setAlpha(1.0F);
            return vibrationsignalparticle;
        }
    }
}
