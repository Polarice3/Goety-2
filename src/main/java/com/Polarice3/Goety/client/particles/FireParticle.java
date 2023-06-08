package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected FireParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
      this.sprites = pSprites;
      this.friction = 0.96F;
      this.xd = this.xd * (double)0.01F + pXSpeed;
      this.yd = this.yd * (double)0.01F + pYSpeed;
      this.zd = this.zd * (double)0.01F + pZSpeed;
      this.quadSize *= 3.0F;
      this.lifetime = 8;
      this.hasPhysics = false;
      this.setSpriteFromAge(pSprites);
   }

   public void tick() {
      super.tick();
      if (this.age < this.lifetime) {
         this.setSpriteFromAge(this.sprites);
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double p_106817_, double p_106818_, double p_106819_) {
      this.setBoundingBox(this.getBoundingBox().move(p_106817_, p_106818_, p_106819_));
      this.setLocationFromBoundingbox();
   }

   public int getLightColor(float p_106821_) {
      float f = ((float)this.age + p_106821_) / (float)this.lifetime;
      f = Mth.clamp(f, 0.0F, 1.0F);
      int i = super.getLightColor(p_106821_);
      int j = i & 255;
      int k = i >> 16 & 255;
      j += (int)(f * 15.0F * 16.0F);
      if (j > 240) {
         j = 240;
      }

      return j | k << 16;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_106827_) {
         this.sprite = p_106827_;
      }

      public Particle createParticle(SimpleParticleType p_106838_, ClientLevel p_106839_, double p_106840_, double p_106841_, double p_106842_, double p_106843_, double p_106844_, double p_106845_) {
         return new FireParticle(p_106839_, p_106840_, p_106841_, p_106842_, p_106843_, p_106844_, p_106845_, this.sprite);
      }
   }

   public static class SmallProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public SmallProvider(SpriteSet p_172113_) {
         this.sprite = p_172113_;
      }

      public Particle createParticle(SimpleParticleType p_172124_, ClientLevel p_172125_, double p_172126_, double p_172127_, double p_172128_, double p_172129_, double p_172130_, double p_172131_) {
         FireParticle flameparticle = new FireParticle(p_172125_, p_172126_, p_172127_, p_172128_, p_172129_, p_172130_, p_172131_, this.sprite);
         flameparticle.scale(0.5F);
         return flameparticle;
      }
   }
}