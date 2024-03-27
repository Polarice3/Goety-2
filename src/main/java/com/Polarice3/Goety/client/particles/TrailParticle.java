package com.Polarice3.Goety.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class TrailParticle extends TextureSheetParticle {
   private static final RandomSource RANDOM = RandomSource.create();
   private final SpriteSet sprites;

   TrailParticle(ClientLevel p_107762_, double p_107763_, double p_107764_, double p_107765_, double p_107766_, double p_107767_, double p_107768_, SpriteSet p_107769_) {
      super(p_107762_, p_107763_, p_107764_, p_107765_, 0.5D - RANDOM.nextDouble(), p_107767_, 0.5D - RANDOM.nextDouble());
      this.friction = 0.96F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = p_107769_;
      this.gravity = 0.0F;
      this.xd = 0.0D;
      this.yd = 0.0D;
      this.zd = 0.0D;
      this.quadSize *= 0.75F;
      this.lifetime = 10;
      this.hasPhysics = false;
      this.setSpriteFromAge(p_107769_);
      if (this.isCloseToScopingPlayer()) {
         this.setAlpha(0.0F);
      }

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

   private boolean isCloseToScopingPlayer() {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer localplayer = minecraft.player;
      return localplayer != null && localplayer.getEyePosition().distanceToSqr(this.x, this.y, this.z) <= 9.0D && minecraft.options.getCameraType().isFirstPerson() && localplayer.isScoping();
   }

   public static class MobProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public MobProvider(SpriteSet p_107826_) {
         this.sprite = p_107826_;
      }

      public Particle createParticle(SimpleParticleType p_107837_, ClientLevel p_107838_, double p_107839_, double p_107840_, double p_107841_, double p_107842_, double p_107843_, double p_107844_) {
         Particle particle = new TrailParticle(p_107838_, p_107839_, p_107840_, p_107841_, p_107842_, p_107843_, p_107844_, this.sprite);
         particle.setColor((float)p_107842_, (float)p_107843_, (float)p_107844_);
         return particle;
      }
   }
}