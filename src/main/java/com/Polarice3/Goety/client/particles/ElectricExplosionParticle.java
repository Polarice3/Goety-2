package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;

public class ElectricExplosionParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected ElectricExplosionParticle(ClientLevel level, double x, double y, double z, float xSpeed, float ySpeed, float zSpeed, SpriteSet spriteSet) {
      super(level, x, y, z, 0.0D, 0.0D, 0.0D);
      this.lifetime = 6 + this.random.nextInt(4);
      this.rCol = xSpeed;
      this.gCol = ySpeed;
      this.bCol = zSpeed;
      this.quadSize = 2.0F;
      this.sprites = spriteSet;
      this.setSpriteFromAge(spriteSet);
   }

   public int getLightColor(float p_106921_) {
      return LightTexture.FULL_BRIGHT;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet p_106925_) {
         this.sprites = p_106925_;
      }

      public Particle createParticle(SimpleParticleType p_106936_, ClientLevel p_106937_, double p_106938_, double p_106939_, double p_106940_, double p_106941_, double p_106942_, double p_106943_) {
         return new ElectricExplosionParticle(p_106937_, p_106938_, p_106939_, p_106940_, (float) p_106941_, (float) p_106942_, (float) p_106943_, this.sprites);
      }
   }
}