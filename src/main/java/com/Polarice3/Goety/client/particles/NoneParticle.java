package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NoneParticle extends TextureSheetParticle {
   private static final RandomSource RANDOM = RandomSource.create();
   private final SpriteSet sprites;

   NoneParticle(ClientLevel p_107762_, double p_107763_, double p_107764_, double p_107765_, double p_107766_, double p_107767_, double p_107768_, SpriteSet p_107769_) {
      super(p_107762_, p_107763_, p_107764_, p_107765_, 0.5D - RANDOM.nextDouble(), p_107767_, 0.5D - RANDOM.nextDouble());
      this.friction = 0.96F;
      this.gravity = -0.1F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = p_107769_;
      this.yd *= (double)0.2F;
      if (p_107766_ == 0.0D && p_107768_ == 0.0D) {
         this.xd *= (double)0.1F;
         this.zd *= (double)0.1F;
      }

      this.quadSize *= 0.0F;
      this.lifetime = 1;
      this.hasPhysics = false;
      this.setSpriteFromAge(p_107769_);
      this.setAlpha(0.0F);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      super.tick();
      this.remove();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_107847_) {
         this.sprite = p_107847_;
      }

      public Particle createParticle(SimpleParticleType p_107858_, ClientLevel p_107859_, double p_107860_, double p_107861_, double p_107862_, double p_107863_, double p_107864_, double p_107865_) {
         return new NoneParticle(p_107859_, p_107860_, p_107861_, p_107862_, p_107863_, p_107864_, p_107865_, this.sprite);
      }
   }
}