package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;

public class CircleExplodeParticle extends GroundCircleParticle {
   private int speed;
   private final SpriteSet spriteSet;

   CircleExplodeParticle(ClientLevel p_233976_, double p_233977_, double p_233978_, double p_233979_, float red, float green, float blue, SpriteSet spriteSet) {
      super(p_233976_, p_233977_, p_233978_, p_233979_, 0.0D, 0.0D, 0.0D);
      this.quadSize = 10.0F;
      this.lifetime = 30;
      this.gravity = 0.0F;
      this.xd = 0.0D;
      this.yd = 0.0D;
      this.zd = 0.0D;
      this.rCol = red;
      this.gCol = green;
      this.bCol = blue;
      this.spriteSet = spriteSet;
      this.setSpriteFromAge(spriteSet);
   }

   public float getQuadSize(float p_234003_) {
      return this.quadSize;
   }

   public int getLightColor(float p_233983_) {
      return LightTexture.FULL_BRIGHT;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_LIT;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.age += this.speed;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.spriteSet);
      }
   }

   public static class Provider implements ParticleProvider<CircleExplodeParticleOption> {
      private final SpriteSet spriteSet;

      public Provider(SpriteSet p_234008_) {
         this.spriteSet = p_234008_;
      }

      public Particle createParticle(CircleExplodeParticleOption p_234019_, ClientLevel p_234020_, double p_234021_, double p_234022_, double p_234023_, double p_234024_, double p_234025_, double p_234026_) {
         CircleExplodeParticle shockwaveParticle = new CircleExplodeParticle(p_234020_, p_234021_, p_234022_, p_234023_, p_234019_.getRed(), p_234019_.getGreen(), p_234019_.getBlue(), this.spriteSet);
         shockwaveParticle.speed = p_234019_.getSpeed();
         shockwaveParticle.quadSize = p_234019_.getSize();
         shockwaveParticle.setAlpha(1.0F);
         return shockwaveParticle;
      }
   }
}