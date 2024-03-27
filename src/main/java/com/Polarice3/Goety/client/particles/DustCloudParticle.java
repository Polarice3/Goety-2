package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

public class DustCloudParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   DustCloudParticle(ClientLevel p_105856_, double p_105857_, double p_105858_, double p_105859_, double p_105860_, double p_105861_, double p_105862_, SpriteSet spriteSet) {
      super(p_105856_, p_105857_, p_105858_, p_105859_);
      this.scale(3.0F);
      this.setSize(0.25F, 0.25F);
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.gravity = 3.0E-6F;
      this.xd = p_105860_;
      this.yd = p_105861_ + (double)(this.random.nextFloat() / 500.0F);
      this.zd = p_105862_;
      this.sprites = spriteSet;
      this.setSpriteFromAge(spriteSet);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.setSpriteFromAge(this.sprites);
      if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
         this.xd += (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
         this.zd += (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
            this.alpha -= 0.015F;
         }
      } else {
         this.remove();
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public static class Provider implements ParticleProvider<DustCloudParticleOption> {
      private final SpriteSet sprites;

      public Provider(SpriteSet p_105878_) {
         this.sprites = p_105878_;
      }

      public Particle createParticle(DustCloudParticleOption p_105889_, ClientLevel p_105890_, double p_105891_, double p_105892_, double p_105893_, double p_105894_, double p_105895_, double p_105896_) {
         DustCloudParticle particle = new DustCloudParticle(p_105890_, p_105891_, p_105892_, p_105893_, p_105894_, p_105895_, p_105896_, this.sprites);
         particle.setColor(p_105889_.getColor().x(), p_105889_.getColor().y(), p_105889_.getColor().z());
         particle.quadSize = p_105889_.getScale();
         particle.setAlpha(0.9F);
         return particle;
      }
   }
}