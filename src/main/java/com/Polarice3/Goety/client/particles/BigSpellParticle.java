package com.Polarice3.Goety.client.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class BigSpellParticle extends TextureSheetParticle {
   private static final RandomSource RANDOM = RandomSource.create();
   private final SpriteSet sprites;
   private final boolean playerBase;

   BigSpellParticle(ClientLevel p_107762_, double p_107763_, double p_107764_, double p_107765_, double p_107766_, double p_107767_, double p_107768_, SpriteSet p_107769_, boolean playerBase) {
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

      this.quadSize *= 2.0F;
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.hasPhysics = false;
      this.setSpriteFromAge(p_107769_);
      this.playerBase = playerBase;
      if (playerBase && this.isCloseToScopingPlayer()) {
         this.setAlpha(0.0F);
      }

   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
      if (this.playerBase && this.isCloseToScopingPlayer()) {
         this.setAlpha(0.0F);
      } else {
         this.setAlpha(Mth.lerp(0.05F, this.alpha, 1.0F));
      }

   }

   private boolean isCloseToScopingPlayer() {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer localplayer = minecraft.player;
      return localplayer != null && localplayer.getEyePosition().distanceToSqr(this.x, this.y, this.z) <= 16.0D && minecraft.options.getCameraType().isFirstPerson();
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_107847_) {
         this.sprite = p_107847_;
      }

      public Particle createParticle(SimpleParticleType p_107858_, ClientLevel p_107859_, double p_107860_, double p_107861_, double p_107862_, double p_107863_, double p_107864_, double p_107865_) {
         return new BigSpellParticle(p_107859_, p_107860_, p_107861_, p_107862_, p_107863_, p_107864_, p_107865_, this.sprite, true);
      }
   }

   public static class MobProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public MobProvider(SpriteSet p_107826_) {
         this.sprite = p_107826_;
      }

      public Particle createParticle(SimpleParticleType p_107837_, ClientLevel p_107838_, double p_107839_, double p_107840_, double p_107841_, double p_107842_, double p_107843_, double p_107844_) {
         Particle particle = new BigSpellParticle(p_107838_, p_107839_, p_107840_, p_107841_, p_107842_, p_107843_, p_107844_, this.sprite, false);
         particle.setColor((float)p_107842_, (float)p_107843_, (float)p_107844_);
         return particle;
      }
   }
}