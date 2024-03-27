package com.Polarice3.Goety.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SpellSquareParticle extends TextureSheetParticle {
   private static final RandomSource RANDOM = RandomSource.create();
   private final SpriteSet sprites;

   SpellSquareParticle(ClientLevel p_107762_, double p_107763_, double p_107764_, double p_107765_, double p_107766_, double p_107767_, double p_107768_, SpriteSet p_107769_) {
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

      this.quadSize *= 1.0F;
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.hasPhysics = false;
      this.setSpriteFromAge(p_107769_);
   }

   public int getLightColor(float p_233983_) {
      return LightTexture.FULL_BRIGHT;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }

   public void render(VertexConsumer p_233985_, Camera p_233986_, float p_233987_) {
      this.alpha = 1.0F - Mth.clamp(((float) this.age) / (float) this.lifetime, 0.0F, 1.0F);
      super.render(p_233985_, p_233986_, p_233987_);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_107826_) {
         this.sprite = p_107826_;
      }

      public Particle createParticle(SimpleParticleType p_107837_, ClientLevel p_107838_, double p_107839_, double p_107840_, double p_107841_, double p_107842_, double p_107843_, double p_107844_) {
         Particle particle = new SpellSquareParticle(p_107838_, p_107839_, p_107840_, p_107841_, p_107842_, p_107843_, p_107844_, this.sprite);
         particle.setColor((float)p_107842_, (float)p_107843_, (float)p_107844_);
         return particle;
      }
   }
}