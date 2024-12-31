package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class BubbleStreamParticle extends RisingParticle {
    public BubbleStreamParticle(ClientLevel p_106800_, double p_106801_, double p_106802_, double p_106803_, double p_106804_, double p_106805_, double p_106806_) {
        super(p_106800_, p_106801_, p_106802_, p_106803_, p_106804_, p_106805_, p_106806_);
        this.setSize(0.02F, 0.02F);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void move(double p_106817_, double p_106818_, double p_106819_) {
        this.setBoundingBox(this.getBoundingBox().move(p_106817_, p_106818_, p_106819_));
        this.setLocationFromBoundingbox();
    }

    public void remove() {
        this.level.addParticle(ParticleTypes.BUBBLE_POP, this.x, this.y, this.z, this.xd, this.yd, this.zd);
        this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.AMBIENT, 1.0F, 1.0F, false);
        super.remove();
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_106827_) {
            this.sprite = p_106827_;
        }

        public Particle createParticle(SimpleParticleType p_106838_, ClientLevel p_106839_, double p_106840_, double p_106841_, double p_106842_, double p_106843_, double p_106844_, double p_106845_) {
            BubbleStreamParticle flameparticle = new BubbleStreamParticle(p_106839_, p_106840_, p_106841_, p_106842_, p_106843_, p_106844_, p_106845_);
            flameparticle.pickSprite(this.sprite);
            return flameparticle;
        }
    }
}
