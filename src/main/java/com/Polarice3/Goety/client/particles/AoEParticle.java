package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;

public class AoEParticle extends GroundCircleParticle {
   private float initialSize;
   private float growing;
   private float finalSize;
   private final SpriteSet spriteSet;

   AoEParticle(ClientLevel p_233976_, double p_233977_, double p_233978_, double p_233979_, float red, float green, float blue, SpriteSet spriteSet) {
      super(p_233976_, p_233977_, p_233978_, p_233979_, 0.0D, 0.0D, 0.0D);
      this.quadSize = 10.0F;
      this.lifetime = 100;
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
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         if (this.growing > 0.0F) {
            if (this.initialSize <= this.finalSize) {
               if (this.quadSize < this.finalSize) {
                  this.quadSize += this.growing;
               }
            } else {
               if (this.quadSize > this.finalSize) {
                  this.quadSize -= this.growing;
               }
            }
         }
         this.setSpriteFromAge(this.spriteSet);
      }
   }

   public static class Provider implements ParticleProvider<AoEParticleOption> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_234008_) {
         this.sprite = p_234008_;
      }

      public Particle createParticle(AoEParticleOption p_234019_, ClientLevel p_234020_, double p_234021_, double p_234022_, double p_234023_, double p_234024_, double p_234025_, double p_234026_) {
         AoEParticle shockwaveParticle = new AoEParticle(p_234020_, p_234021_, p_234022_, p_234023_, (float) p_234024_, (float) p_234025_, (float) p_234026_, sprite);
         shockwaveParticle.initialSize = p_234019_.getSize();
         shockwaveParticle.quadSize = p_234019_.getSize();
         shockwaveParticle.growing = p_234019_.getGrowing();
         shockwaveParticle.finalSize = p_234019_.getMaxSize();
         shockwaveParticle.setLifetime(p_234019_.getLife());
         shockwaveParticle.setAlpha(0.25F);
         return shockwaveParticle;
      }
   }
}